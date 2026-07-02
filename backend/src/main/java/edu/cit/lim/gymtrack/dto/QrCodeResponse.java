package edu.cit.lim.gymtrack.dto;

public class QrCodeResponse {
    private String qrData;
    private String qrImageBase64;

    public QrCodeResponse(String qrData, String qrImageBase64) {
        this.qrData = qrData;
        this.qrImageBase64 = qrImageBase64;
    }

    public String getQrData() { return qrData; }

    public String getQrImageBase64() { return qrImageBase64; }
}
