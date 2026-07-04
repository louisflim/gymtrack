package edu.cit.lim.gymtrack.service;

import edu.cit.lim.gymtrack.dto.CheckoutResponse;
import edu.cit.lim.gymtrack.dto.PaymentResponse;
import edu.cit.lim.gymtrack.entity.Payment;
import edu.cit.lim.gymtrack.entity.PaymentStatus;
import edu.cit.lim.gymtrack.entity.SubscriptionPlan;
import edu.cit.lim.gymtrack.entity.User;
import edu.cit.lim.gymtrack.entity.Gym;
import edu.cit.lim.gymtrack.repository.PaymentRepository;
import edu.cit.lim.gymtrack.repository.UserRepository;
import edu.cit.lim.gymtrack.util.GymGuard;
import edu.cit.lim.gymtrack.util.RoleGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PlanService planService;
    private final PayMongoService payMongoService;
    private final MembershipService membershipService;

    public PaymentService(PaymentRepository paymentRepository,
                          UserRepository userRepository,
                          PlanService planService,
                          PayMongoService payMongoService,
                          MembershipService membershipService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.planService = planService;
        this.payMongoService = payMongoService;
        this.membershipService = membershipService;
    }

    @Transactional
    public CheckoutResponse createCheckout(Long planId, String memberEmail) {
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        SubscriptionPlan plan = planService.getActivePlan(planId);

        if (member.getGym() == null) {
            throw new IllegalArgumentException(
                    "Visit a gym and scan the staff QR code to enroll before purchasing a plan."
            );
        }
        if (plan.getGym() == null || !plan.getGym().getId().equals(member.getGym().getId())) {
            throw new IllegalArgumentException("This plan is not available for your gym.");
        }

        Payment payment = new Payment();
        payment.setUser(member);
        payment.setPlan(plan);
        payment.setAmount(plan.getPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        String reference = "GT-" + payment.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
        PayMongoService.CheckoutSessionResult session = payMongoService.createCheckoutSession(plan, reference);
        payment.setPaymongoCheckoutId(session.checkoutId());
        paymentRepository.save(payment);

        return new CheckoutResponse(payment.getId(), session.checkoutUrl(), payment.getStatus().name());
    }

    @Transactional
    public void handleWebhook(String payload) {
        if (!payMongoService.isPaidEvent(payload)) {
            return;
        }

        String checkoutId = payMongoService.extractCheckoutIdFromWebhook(payload);
        if (checkoutId == null) {
            return;
        }

        Payment payment = paymentRepository.findByPaymongoCheckoutId(checkoutId)
                .orElseGet(() -> resolveMockPayment(checkoutId));
        if (payment == null || payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(java.time.LocalDateTime.now());
        payment.setPaymentMethod(payMongoService.extractPaymentMethod(payload));
        paymentRepository.save(payment);

        membershipService.activateMembership(payment.getUser().getId(), payment.getPlan().getId());
    }

    @Transactional
    public void confirmMockPayment(String reference) {
        if (reference == null || !reference.startsWith("GT-")) {
            throw new IllegalArgumentException("Invalid payment reference.");
        }
        Long paymentId = Long.parseLong(reference.split("-")[1]);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(java.time.LocalDateTime.now());
        payment.setPaymentMethod("mock");
        paymentRepository.save(payment);
        membershipService.activateMembership(payment.getUser().getId(), payment.getPlan().getId());
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> myPayments(String memberEmail) {
        User member = RoleGuard.requireMember(userRepository, memberEmail);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(member.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> allPayments(String requesterEmail) {
        User requester = RoleGuard.requireAdmin(userRepository, requesterEmail);
        Gym gym = GymGuard.requireAdminGym(requester);

        return paymentRepository.findByPlanGymIdOrderByCreatedAtDesc(gym.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private Payment resolveMockPayment(String checkoutId) {
        if (!checkoutId.startsWith("mock_GT-")) {
            return paymentRepository.findByPaymongoCheckoutId(checkoutId).orElse(null);
        }
        String reference = checkoutId.replace("mock_", "");
        try {
            Long paymentId = Long.parseLong(reference.split("-")[1]);
            return paymentRepository.findById(paymentId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUser().getFullName(),
                payment.getPlan().getName(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getPaymentMethod(),
                payment.getCreatedAt(),
                payment.getPaidAt()
        );
    }
}
