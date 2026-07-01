
GymTrack - Gym Membership Management System
📋 Overview
GymTrack is a comprehensive web and mobile-based Gym Membership Management System designed to digitalize and streamline gym operations for small to medium-sized gyms in the Philippines. The system provides secure role-based access, subscription management, QR code attendance tracking, and integrated online payment processing.

🎯 Project Objectives
General Objective
To develop a web and mobile-based Gym Membership Management System that digitalizes member registration, subscription tracking, attendance monitoring, and online payment processing for small to medium-sized gyms with a secure and structured account management system.

Specific Objectives
Implement a secure registration system with role-based access control

Allow gym administrators to create and manage custom subscription tiers

Enable gym staff to manage member profiles with QR code generation

Implement QR code-based attendance tracking

Integrate PayMongo as an online payment gateway

Provide an Admin dashboard with key metrics and reports

Deploy the system online accessible on both web and mobile platforms

👥 Target Users
User	Role Description
Gym Owner (Admin)	Full system access. Manages staff accounts, member accounts, subscription plans, payments, and views the dashboard.
Gym Staff	Created by Admin. Manages members, assigns subscription plans, and scans QR codes for attendance.
Gym Member	Self-registers. Pays for subscriptions online, views own profile, QR code, attendance history, and payment records.
✨ Key Features
1. User Authentication and Account Management
Single registration form with role dropdown (Gym Owner or Member only)

Staff accounts created exclusively by Admin

Secure email and password authentication

Role-based access control (Admin, Staff, Member)

2. Member Management
Auto-creation of member profiles upon registration

Unique QR code generation per member

View, search, filter, and edit member profiles

Display subscription status and payment history

3. Subscription Plan Management
Admin creates and manages subscription tiers (name, duration, price)

Activate or deactivate subscription plans

Auto-calculated expiry dates

Membership status tracking (Active, Expiring Soon, Expired)

4. Online Payment
PayMongo integration (GCash, Maya, Credit/Debit Card)

Webhook-based automatic subscription activation

Secure payment record storage

Admin view of all payment transactions

5. Attendance Tracking
QR code check-in and check-out via device camera

Automatic recording of attendance timestamps

Member name, plan, and status display after scanning

Restriction for expired or unpaid memberships

Filter attendance logs by date or member

6. Admin Dashboard
Total members, active subscriptions, and expired memberships

Today's check-in count and total payments collected

Staff and Member account management

Attendance log filtering

🛠️ Technology Stack
Backend
Language: Java

Framework: Spring Boot

Database: Supabase (PostgreSQL)

Authentication: JWT

API: RESTful

Web Application
Framework: ReactJS

Compatibility: Chrome, Firefox, Edge

Mobile Application
Platform: Android 8.0 (Oreo) and above

Language: Kotlin

Libraries: Retrofit (API), ZXing (QR Code)

IDE: Android Studio

Payment Integration
Gateway: PayMongo

Methods: GCash, Maya, Credit/Debit Card

📊 System Architecture
text
┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │
│   Web App       │     │   Mobile App    │
│   (ReactJS)     │     │   (Android)     │
│                 │     │                 │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───────────┬───────────┘
                     │
              ┌──────▼──────┐
              │             │
              │  REST API   │
              │ Spring Boot │
              │             │
              └──────┬──────┘
                     │
          ┌──────────┼──────────┐
          │          │          │
    ┌─────▼─────┐ ┌──▼───┐ ┌───▼────┐
    │           │ │      │ │        │
    │  Supabase │ │JWT   │ │PayMongo│
    │  Database │ │      │ │        │
    │           │ │      │ │        │
    └───────────┘ └──────┘ └────────┘
🚀 Getting Started
Prerequisites
Java 11 or higher

Node.js and npm

Android Studio

Supabase account

PayMongo account

Backend Setup
bash
# Clone the repository
git clone [repository-url]

# Navigate to backend directory
cd gymtrack-backend

# Configure application.properties
# Update Supabase and PayMongo credentials

# Build and run
./mvnw spring-boot:run
Web Application Setup
bash
# Navigate to web app directory
cd gymtrack-web

# Install dependencies
npm install

# Start development server
npm start
Mobile Application Setup
Open Android Studio

Open the Android project

Update API endpoint in Retrofit configuration

Build and run on emulator or physical device

📋 Functional Requirements Summary
Feature	Requirements Count
User Authentication	FR-001 to FR-008
Member Management	FR-009 to FR-012
Subscription Plans	FR-013 to FR-017
Online Payment	FR-018 to FR-022
Attendance Tracking	FR-023 to FR-028
Admin Dashboard	FR-029 to FR-033
🔒 Non-Functional Requirements
Performance: Page loads under 3 seconds, supports 20+ concurrent users

Security: JWT authentication, BCrypt encryption, HTTPS

Usability: Intuitive interface, clear error messages

Reliability: 95% uptime, immutable payment records

Maintainability: Controller-Service-Repository pattern, RESTful APIs

📝 Limitations
Staff accounts cannot self-register (Admin-created only)

PayMongo-supported payment methods only

No cash payment recording or POS integration

No workout planning or fitness tracking features

No biometric or fingerprint-based attendance

Android-only mobile application

Single-branch gym management only

🗓️ Project Timeline
Week	Planned Activity
Week 1	Proposal preparation and approval
Week 2	Requirements refinement
Week 3	UI and database design
Week 4	Backend setup
📄 Documentation
Software Requirements Specification (SRS)

Entity Relationship Diagram (ERD)

Use Case Models

Activity Diagrams

Class Diagrams

Sequence Diagrams

👨‍💻 Developer
Louis Francis Lim
IT342-G01 - Systems Integration and Architecture 1

📅 Version History
Version	Date	Description
1.0	06/23/2026	Initial SRS submission
2.0	06/30/2026	Revised SRS with updated requirements and specifications
📧 Contact
For questions or support, please contact the developer.

© 2026 GymTrack - All Rights Reserved

that i can paste in github
GymTrack - Gym Membership Management System
📋 Overview
GymTrack is a comprehensive web and mobile-based Gym Membership Management System designed to digitalize and streamline gym operations for small to medium-sized gyms in the Philippines. The system provides secure role-based access, subscription management, QR code attendance tracking, and integrated online payment processing.

🎯 Project Objectives
General Objective
To develop a web and mobile-based Gym Membership Management System that digitalizes member registration, subscription tracking, attendance monitoring, and online payment processing for small to medium-sized gyms with a secure and structured account management system.

Specific Objectives
Implement a secure registration system with role-based access control

Allow gym administrators to create and manage custom subscription tiers

Enable gym staff to manage member profiles with QR code generation

Implement QR code-based attendance tracking

Integrate PayMongo as an online payment gateway

Provide an Admin dashboard with key metrics and reports

Deploy the system online accessible on both web and mobile platforms

👥 Target Users
User	Role Description
Gym Owner (Admin)	Full system access. Manages staff accounts, member accounts, subscription plans, payments, and views the dashboard.
Gym Staff	Created by Admin. Manages members, assigns subscription plans, and scans QR codes for attendance.
Gym Member	Self-registers. Pays for subscriptions online, views own profile, QR code, attendance history, and payment records.
✨ Key Features
1. User Authentication and Account Management
Single registration form with role dropdown (Gym Owner or Member only)

Staff accounts created exclusively by Admin

Secure email and password authentication

Role-based access control (Admin, Staff, Member)

2. Member Management
Auto-creation of member profiles upon registration

Unique QR code generation per member

View, search, filter, and edit member profiles

Display subscription status and payment history

3. Subscription Plan Management
Admin creates and manages subscription tiers (name, duration, price)

Activate or deactivate subscription plans

Auto-calculated expiry dates

Membership status tracking (Active, Expiring Soon, Expired)

4. Online Payment
PayMongo integration (GCash, Maya, Credit/Debit Card)

Webhook-based automatic subscription activation

Secure payment record storage

Admin view of all payment transactions

5. Attendance Tracking
QR code check-in and check-out via device camera

Automatic recording of attendance timestamps

Member name, plan, and status display after scanning

Restriction for expired or unpaid memberships

Filter attendance logs by date or member

6. Admin Dashboard
Total members, active subscriptions, and expired memberships

Today's check-in count and total payments collected

Staff and Member account management

Attendance log filtering

🛠️ Technology Stack
Backend
Language: Java

Framework: Spring Boot

Database: Supabase (PostgreSQL)

Authentication: JWT

API: RESTful

Web Application
Framework: ReactJS

Compatibility: Chrome, Firefox, Edge

Mobile Application
Platform: Android 8.0 (Oreo) and above

Language: Kotlin

Libraries: Retrofit (API), ZXing (QR Code)

IDE: Android Studio

Payment Integration
Gateway: PayMongo

Methods: GCash, Maya, Credit/Debit Card
