Mohammed saifain -
abdulelah al zahrani - 
albarra alharbi - 
abdulaziz al malki - 
reda alamoudi

# 💊 MediTrack: Comprehensive Android Medication Tracker

A University Software Engineering Capstone Project.

MediTrack is a robust native Android application engineered to enhance patient adherence to medication schedules and provide doctors with an efficient platform for prescription management and patient oversight.

---

## ⭐ Project Status Badges

| Status | Badge |
| :--- | :--- |
| **Project Version** | ![Version](https://img.shields.io/badge/Version-1.0.0-blue) |
| **Built With** | ![Java](https://img.shields.io/badge/Language-Java-brightgreen) |
| **Database** | ![Firebase Firestore](https://img.shields.io/badge/Database-Firestore-yellow) |
| **License** | ![License](https://img.shields.io/badge/License-MIT-purple) |

---

## 💡 Project Overview

MediTrack provides a seamless, secure connection between doctors and patients. The application digitizes the prescription workflow, ensuring patients never miss a dose while giving physicians full control over patient treatment plans.

The project was developed as a requirement for a Software Engineering university course, focusing on system architecture, database design, and mobile application stability.

## 👥 Target Users & Value Proposition

| Role | Description |
| :--- | :--- |
| 🧑‍⚕️ **Doctors** | Seamlessly add patients via email, create, manage, and monitor active prescriptions, and send instant notifications for plan changes. |
| 👤 **Patients** | Access daily medication schedules, receive critical reminders, manage communication with their physician, and approve/reject doctor invitations. |

---

## ✨ Core Features

### 👨‍⚕️ Doctor Capabilities
* **Secure Invitations:** Implement a robust invitation system to link patients using email and UID verification.
* **Prescription Management:** Full CRUD (Create, Read, Update, Delete) functionality for patient medications.
* **Active Status Filtering:** View and manage only active patients and prescriptions.
* **Instant Notifications:** Send critical push notifications to patients upon adding or updating a prescription.

### 👤 Patient Experience
* **Daily Dose View:** Clear, organized view of medications required for the current day.
* **Upcoming Medicine:** Algorithm to identify and display the next imminent dose time.
* **Medication Calendar:** Comprehensive calendar view showing scheduled medications filtered by date and day of the week.
* **Invitation Management:** Ability to accept or reject doctor linkage requests.
* **Future Improvement:** Integrated Local Notifications for reliable medication reminders (using `AlarmManager`).

---

## 🛠 Tech Stack

The application is built entirely on native Android with a focus on stability and Firebase integration.

| Technology | Purpose | Icon |
| :--- | :--- | :--- |
| **Java** | Primary programming language for Android development. | ☕ |
| **Android SDK** | Native Android UI/UX and system functionalities. | 🤖 |
| **Firebase Firestore** | NoSQL cloud database for real-time data storage (users, prescriptions, invitations). | 🔥 |
| **Firebase Auth** | Secure user authentication and management. | 🔒 |
| **AlarmManager** | Utilized for scheduled local medication reminders. | ⏰ |

---

## 📂 Project Structure

The codebase follows a standard component-based architecture for maintainability and scalability.

---

## ▶️ Getting Started (For Developers)

To run the project locally, ensure you have Android Studio installed.

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/SkobyM/mediTrackProject.git](https://github.com/SkobyM/mediTrackProject.git)
    ```
2.  **Firebase Setup:**
    * Create a new Firebase project.
    * Enable **Firestore Database** and **Firebase Authentication**.
    * Download your `google-services.json` file and place it in the `app/` directory.
3.  **Run:** Open the project in Android Studio and run on an emulator or physical device.

---

## 📌 Future Improvements

The following features are planned for future iterations:

* **Local Notification Implementation:** Integrate `AlarmManager` for recurring, reliable medication time reminders.
* **Settings Interface:** Full user settings and profile customization options.
* **UI/UX Enhancement:** Support for Dark Mode and enhanced accessibility features.

---

## 📧 Contact

If you have any questions, suggestions, or require further development assistance, feel free to reach out.

🔗 **Repository Link:** [https://github.com/SkobyM]
