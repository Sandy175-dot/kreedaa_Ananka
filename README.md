# 🏟️ Kreeda-Ankana: The Modern Athlete's Arena

**Kreeda-Ankana** is a premium, startup-grade sports management and turf booking application. Designed for modern athletes, it streamlines ground reservations, team management, and competitive challenges into a single, cohesive ecosystem.

---

## 📸 Visuals & Experience

### 🔐 Premium Onboarding
Minimalist, high-contrast authentication flow designed for a professional first impression.
<img width="501" height="700" alt="Screenshot 2026-05-15 164346" src="https://github.com/user-attachments/assets/9c690c70-45a2-4717-848a-dc206c4aff67" />


### 🏠 Athlete Dashboard
<img width="466" height="600" alt="Screenshot 2026-05-15 164500" src="https://github.com/user-attachments/assets/b0230d8c-e841-4cfb-b98e-6a61607ee5c1" />


### 📅 Real-Time Slot Booking
A visual reservation system with calendar integration and instant slot synchronization.
<img width="464" height="600" alt="Screenshot 2026-05-15 164528" src="https://github.com/user-attachments/assets/dc04d7a8-05bd-4de9-9459-09165bac0628" />
<img width="447" height="600" alt="Screenshot 2026-05-15 164633" src="https://github.com/user-attachments/assets/f7ff9f4c-7668-45a2-a942-0d265c255954" />

### ⚔️ Challenge Arena & Rankings
Engage with the community through match challenges and track your team's progress on the global leaderboard.
<img width="460" height="914" alt="Screenshot 2026-05-15 164554" src="https://github.com/user-attachments/assets/301ceb00-c81d-4d20-84ed-08389b8dd148" />
<img width="450" height="910" alt="Screenshot 2026-05-15 164710" src="https://github.com/user-attachments/assets/9fc10b4d-cbff-43a5-9479-423c28b4219e" />


### 👤 Athlete Profile
Comprehensive user stats and management at a glance.
<img width="460" height="921" alt="Screenshot 2026-05-15 164737" src="https://github.com/user-attachments/assets/01b952b5-c3b3-4d37-b65f-42f7b2eacea1" />

---

## 🔥 Key Features

- **Visual Slot Grid**: Select time slots visually with color-coded availability (Available, Booked, Selected).
- **Duplicate Prevention**: Real-time Firestore logic ensures no two users can book the same slot simultaneously.
- **Team & Player Management**: Create teams, manage player rosters, and track individual performance stats (Runs, Wickets, Matches).
- **In-App Notifications**: Instant confirmation for bookings and challenge updates.
- **Dynamic Analytics**: Real-time calculation of Win Rates, Recent Form (W-L-D), and total activity.
- **Modern UI System**: Built with a "Nothing OS" inspired aesthetic—dark mode, high-contrast typography, and floating navigation.

---

## 🛠 Tech Stack

- **Frontend**: Kotlin, XML (ViewBinding)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase Authentication, Cloud Firestore
- **Real-time**: addSnapshotListener for live slot and score updates
- **Design Components**: Material 3, Shimmer (Facebook), Lottie Animations
- **Libraries**: Lifecycle-KTX, Coroutines, Navigation Component

---

## 🎨 Design System: "Athlete Pro"

The UI has been handcrafted to move away from generic templates, focusing on:
- **Palette**: Deep Onyx (`#0B0D12`) base with Vivid Purple (`#7C4DFF`) and Electric Cyan (`#00D1FF`) accents.
- **Typography**: Clean, bold headings with compact spacing for a high-density, professional feel.
- **Geometry**: Consistent 12dp radii and 1dp subtle strokes for layered depth.

---

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Sandy175-dot/kreedaa_Ananka.git
   ```
2. **Setup Firebase**:
   - Create a new project in the [Firebase Console](https://console.firebase.google.com/).
   - Add your `google-services.json` to the `app/` directory.
   - Enable Email/Password Authentication and Cloud Firestore.
3. **Build & Run**: Open the project in Android Studio and run it on your device or emulator.

---

## 🛡️ License

Distributed under the MIT License. See `LICENSE` for more information.
