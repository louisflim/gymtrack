"""Generate architecture diagram PNG for the implementation report."""
from pathlib import Path
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

OUT = Path(__file__).resolve().parent / "architecture_diagram.png"

fig, ax = plt.subplots(figsize=(10, 7))
ax.set_xlim(0, 10)
ax.set_ylim(0, 10)
ax.axis("off")
ax.set_facecolor("#0B0B0D")

def box(x, y, w, h, text, color="#C8FF3D", text_color="#0B0B0D"):
    rect = mpatches.FancyBboxPatch(
        (x, y), w, h, boxstyle="round,pad=0.02",
        linewidth=1.5, edgecolor="#2A2A2E", facecolor=color
    )
    ax.add_patch(rect)
    ax.text(x + w / 2, y + h / 2, text, ha="center", va="center",
            fontsize=9, fontweight="bold", color=text_color, wrap=True)

def arrow(x1, y1, x2, y2):
    ax.annotate("", xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(arrowstyle="->", color="#8A8A8E", lw=1.5))

box(0.8, 7.5, 3.2, 1.2, "ReactJS\nWeb App", "#1a1a1e", "#F2F2ED")
box(6.0, 7.5, 3.2, 1.2, "Android Kotlin\nMobile App", "#1a1a1e", "#F2F2ED")
box(2.5, 5.0, 5.0, 1.4, "Spring Boot REST API\n(JWT + Controllers/Services)", "#C8FF3D")
box(0.5, 2.0, 2.8, 1.2, "Supabase\nPostgreSQL", "#1a1a1e", "#F2F2ED")
box(3.6, 2.0, 2.8, 1.2, "PayMongo\nPayment Gateway", "#1a1a1e", "#F2F2ED")
box(6.7, 2.0, 2.8, 1.2, "ZXing QR\nGeneration/Scan", "#1a1a1e", "#F2F2ED")

arrow(2.4, 7.5, 4.0, 6.4)
arrow(7.6, 7.5, 6.0, 6.4)
arrow(3.5, 5.0, 1.9, 3.2)
arrow(5.0, 5.0, 5.0, 3.2)
arrow(6.5, 5.0, 8.1, 3.2)

ax.text(5, 0.5, "GymTrack System Architecture", ha="center", fontsize=12,
        fontweight="bold", color="#C8FF3D")

plt.tight_layout()
plt.savefig(OUT, dpi=150, facecolor="#0B0B0D", bbox_inches="tight")
print(f"Saved {OUT}")
