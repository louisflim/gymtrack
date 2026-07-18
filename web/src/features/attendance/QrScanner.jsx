import { useEffect, useRef } from "react";
import { BrowserMultiFormatReader } from "@zxing/browser";

/** Decode-loop noise — keep scanning; do not close the camera. */
const IGNORABLE_SCAN_ERRORS = new Set([
  "NotFoundException",
  "ChecksumException",
  "FormatException",
  "ReaderException",
]);

function stopVideoStream(videoEl) {
  const stream = videoEl?.srcObject;
  if (stream && typeof stream.getTracks === "function") {
    stream.getTracks().forEach((track) => track.stop());
  }
  if (videoEl) {
    videoEl.srcObject = null;
  }
}

function QrScanner({ open, onScanSuccess, onClose, statusMessage }) {
  const videoRef = useRef(null);
  const controlsRef = useRef(null);
  const scannerRef = useRef(null);
  const onScanSuccessRef = useRef(onScanSuccess);

  useEffect(() => {
    onScanSuccessRef.current = onScanSuccess;
  }, [onScanSuccess]);

  useEffect(() => {
    if (!open) {
      return undefined;
    }

    const scanner = new BrowserMultiFormatReader();
    scannerRef.current = scanner;
    let isActive = true;
    let ignoreFurtherScans = false;

    const releaseCamera = () => {
      try {
        controlsRef.current?.stop();
      } catch (_stopErr) {
        // Ignore double-stop races on unmount/close.
      } finally {
        controlsRef.current = null;
      }
      try {
        scanner.reset();
      } catch (_resetErr) {
        // Ignore cleanup reset races.
      }
      stopVideoStream(videoRef.current);
    };

    const startScanner = async () => {
      // Wait one frame so the <video> element is mounted before ZXing attaches.
      await new Promise((resolve) => requestAnimationFrame(() => resolve()));
      if (!isActive || !videoRef.current) {
        return;
      }

      try {
        controlsRef.current = await scanner.decodeFromVideoDevice(
          undefined,
          videoRef.current,
          (result, error) => {
            if (!isActive) {
              return;
            }

            if (result && !ignoreFurtherScans) {
              ignoreFurtherScans = true;
              onScanSuccessRef.current(result.getText());
              releaseCamera();
              return;
            }

            // Continuous decode fires NotFound/Checksum/Format constantly — ignore those.
            if (error && !IGNORABLE_SCAN_ERRORS.has(error.name)) {
              console.warn("QR scanner decode warning:", error.name || error);
            }
          }
        );
      } catch (_err) {
        if (isActive) {
          onScanSuccessRef.current(
            null,
            "We couldn't access your camera. Please allow camera permission and try again."
          );
        }
        releaseCamera();
      }
    };

    startScanner();

    return () => {
      isActive = false;
      releaseCamera();
      scannerRef.current = null;
    };
  }, [open]);

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
        <video ref={videoRef} className="scanner-video" muted playsInline />
        <p className="scanner-help">{statusMessage || "Align the QR code inside the camera view."}</p>
      </div>
    </div>
  );
}

export default QrScanner;
