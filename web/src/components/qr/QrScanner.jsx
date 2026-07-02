import { useEffect, useRef } from "react";
import { BrowserMultiFormatReader } from "@zxing/browser";

function QrScanner({ open, onScanSuccess, onClose, statusMessage }) {
  const videoRef = useRef(null);
  const controlsRef = useRef(null);

  useEffect(() => {
    if (!open) {
      return undefined;
    }

    const scanner = new BrowserMultiFormatReader();
    let isActive = true;
    let ignoreFurtherScans = false;
    const safeStop = () => {
      try {
        controlsRef.current?.stop();
      } catch (_stopErr) {
        // Ignore double-stop races on unmount/close.
      } finally {
        controlsRef.current = null;
      }
    };

    const startScanner = async () => {
      try {
        controlsRef.current = await scanner.decodeFromVideoDevice(null, videoRef.current, (result, error) => {
          if (!isActive) {
            return;
          }

          if (result && !ignoreFurtherScans) {
            ignoreFurtherScans = true;
            onScanSuccess(result.getText());
            safeStop();
          }

          if (error && error.name !== "NotFoundException") {
            onScanSuccess(null, "Camera scan failed. Please try again.");
            safeStop();
          }
        });
      } catch (_err) {
        if (isActive) {
          onScanSuccess(null, "Unable to access camera. Check browser permission.");
        }
      }
    };

    startScanner();

    return () => {
      isActive = false;
      safeStop();
      try {
        scanner.reset();
      } catch (_resetErr) {
        // Ignore cleanup reset races.
      }
    };
  }, [open, onScanSuccess]);

  if (!open) {
    return null;
  }

  return (
    <div className="scanner-overlay">
      <div className="scanner-modal">
        <div className="scanner-header">
          <h3>Scan GymTrack QR</h3>
          <button type="button" className="scanner-close" onClick={onClose}>
            Close
          </button>
        </div>
        <video ref={videoRef} className="scanner-video" muted />
        <p className="scanner-help">{statusMessage || "Align the QR code inside the camera view."}</p>
      </div>
    </div>
  );
}

export default QrScanner;
