# 🏟️ Kreeda-Ankana: The Modern Athlete's Arena

**Kreeda-Ankana** is a premium, startup-grade sports management and turf booking application. Designed for modern athletes, it streamlines ground reservations, team management, and competitive challenges into a single, cohesive ecosystem.

---

## 📸 Visuals & Experience

### 🔐 Premium Onboarding
Minimalist, high-contrast authentication flow designed for a professional first impression.
<p align="center">
  <img src="screenshots/login.png" width="300" alt="Login Screen" />
</p>

### 🏠 Athlete Dashboard
A central hub featuring dynamic stats, quick actions, and real-time activity tracking.
<p align="center">
  <img src="screenshots/dashboard.png" width="300" alt="Home Screen" />
</p>

### 📅 Real-Time Slot Booking
A visual reservation system with calendar integration and instant slot synchronization.
<p align="center">
  <img src="screenshots/booking.png" width="300" alt="Reservation Calendar" />
  <img src="screenshots/reservations.png" width="300" alt="My Bookings List" />
</p>

### ⚔️ Challenge Arena & Rankings
Engage with the community through match challenges and track your team's progress on the global leaderboard.
<p align="center">
  <img src="screenshots/challenges.png" width="300" alt="Challenge Board" />
  <img src="screenshots/rankings.png" width="300" alt="Leaderboard" />
</p>

### 👤 Athlete Profile
Comprehensive user stats and management at a glance.
<p align="center">
  <img src="screenshots/profile.png" width="300" alt="User Profile" />
</p>

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
