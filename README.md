# 📚 Library Management System
### Android Application : ROHIT NAGAR

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Min%20SDK-API%2024-blue?style=for-the-badge&logo=android"/>
  <img src="https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Status-Completed-success?style=flat-square"/>
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square"/>
  <img src="https://img.shields.io/badge/Assessment-Acxiom%20Consulting-orange?style=flat-square"/>
</p>

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [App Architecture](#-app-architecture)
- [Screens & Navigation Flow](#-screens--navigation-flow)
- [Tech Stack](#-tech-stack)
- [Firebase Data Structure](#-firebase-data-structure)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Login Credentials](#-login-credentials)
- [Module Breakdown](#-module-breakdown)
- [Business Rules & Validations](#-business-rules--validations)
- [Book Category Codes](#-book-category-codes)
- [Screenshots](#-screenshots)
- [Assessment Requirements Coverage](#-assessment-requirements-coverage)
- [Author](#-author)

---

## 🌟 Overview

The **Library Management System** is a fully functional Android application developed as part of the **Acxiom Consulting Technical Assessment**. It enables a library to manage books, movies, memberships, and transactions digitally — replacing manual paper-based processes.

The app supports **two user roles** — Admin and User — with role-based access control enforced at both the UI and database level. All data is stored and synced in real-time using **Firebase Cloud Firestore** with offline persistence support.

---

## ✨ Features

### 🔐 Authentication
- Secure login for Admin and Regular User
- Passwords are hidden during input (as per assessment requirement)
- Session persistence across app restarts
- Role-based routing post-login

### 📦 Transactions
- **Book Availability Check** — Search by Book Name and/or Author with dropdown suggestions
- **Book Issue** — Issue books with auto-populated author, smart date constraints (max 15 days)
- **Return Book** — Return with mandatory Serial No validation, auto-populated dates
- **Pay Fine** — Automatic fine calculation (₹5/day overdue), checkbox confirmation for fine clearance

### 📊 Reports
- Master List of Books
- Master List of Movies
- Master List of Memberships
- Active Issues
- Overdue Returns (with calculated fines)
- Pending Issue Requests

### ⚙️ Maintenance *(Admin Only)*
- Add & Update Memberships (6 Months / 1 Year / 2 Years)
- Add & Update Books and Movies
- User Management (Add/Update users, toggle Admin & Active status)

### 🛡️ Access Control
| Feature | Admin | User |
|---------|:-----:|:----:|
| Maintenance Module | ✅ | ❌ |
| Reports | ✅ | ✅ |
| Transactions | ✅ | ✅ |

---

## 🏗️ App Architecture

```
┌─────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                 │
│         Activities + XML Layouts + Adapters          │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                    BUSINESS LAYER                    │
│       SessionManager + FirebaseHelper + Utils        │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                     DATA LAYER                       │
│          Firebase Firestore (Cloud + Offline)        │
└─────────────────────────────────────────────────────┘
```

**Pattern:** MVP-lite (Model–View–Presenter simplified)
**Data Flow:** Activity → FirebaseHelper → Firestore → Callback → Update UI

---

## 🗺️ Screens & Navigation Flow

```
                    ┌─────────────┐
                    │   SPLASH    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │    LOGIN    │
                    └──────┬──────┘
               ┌───────────┴───────────┐
               │ isAdmin=true          │ isAdmin=false
        ┌──────▼──────┐         ┌──────▼──────┐
        │  ADMIN HOME │         │  USER HOME  │
        │ Maintenance │         │  Reports    │
        │ Reports     │         │ Transactions│
        │ Transactions│         └─────────────┘
        └──────┬──────┘
               │
    ┌──────────┼──────────────────┐
    │          │                  │
    ▼          ▼                  ▼
MAINTENANCE  REPORTS         TRANSACTIONS
    │          │                  │
    ├─Add Mem  ├─Books        ┌───▼────────────┐
    ├─Upd Mem  ├─Movies       │ Book Available │
    ├─Add Book ├─Memberships  └───────┬────────┘
    ├─Upd Book ├─Active Issue         │
    └─User Mgmt├─Overdue          ┌───▼────────── ┐
               └─Requests         │Search Results │
                                  └───────┬───────┘
                                          │
                                     ┌────▼──────┐
                                     │Book Issue │
                                     └────┬──────┘
                                          │
                                    ┌─────▼──────┐
                                    │Return Book │
                                    └─────┬──────┘
                                          │ (always)
                                     ┌────▼──────┐
                                     │  Pay Fine │
                                     └─────┬─────┘
                                           │
                                    ┌──────▼──────┐
                                    │CONFIRMATION │
                                    └─────────────┘
```

---

## 🛠️ Tech Stack

| Technology | Usage |
|-----------|-------|
| **Java** | Primary programming language |
| **Android Studio Hedgehog+** | IDE |
| **Firebase Cloud Firestore** | NoSQL cloud database with real-time sync |
| **Firebase Authentication** | Custom user auth via Firestore queries |
| **Material Design 3** | UI component library |
| **RecyclerView** | All list/table screens |
| **CardView** | Home page module cards |
| **ConstraintLayout** | Primary XML layout engine |
| **SharedPreferences** | Local session management |
| **Firestore Offline Persistence** | Works without internet |

---

## 🔥 Firebase Data Structure

### Collections Overview

```
firestore/
├── users/
│   └── {docId}/
│       ├── username: String
│       ├── password: String
│       ├── name: String
│       ├── isAdmin: Boolean
│       └── isActive: Boolean
│
├── books/
│   └── {docId}/
│       ├── serialNo: String        // e.g. "SC-B-000001"
│       ├── name: String
│       ├── authorName: String
│       ├── category: String        // Science / Economics / Fiction / Children / Personal Development
│       ├── type: String            // "book" or "movie"
│       ├── status: String          // available / issued / damaged / lost
│       ├── cost: Double
│       ├── procurementDate: Timestamp
│       └── quantity: Integer
│
├── memberships/
│   └── {docId}/
│       ├── membershipId: String    // "MEM001"
│       ├── firstName: String
│       ├── lastName: String
│       ├── contactNumber: String
│       ├── contactAddress: String
│       ├── aadharCardNo: String
│       ├── membershipType: String  // 6months / 1year / 2years
│       ├── status: String          // active / inactive
│       ├── startDate: Timestamp
│       ├── endDate: Timestamp
│       └── amountPendingFine: Double
│
├── issues/
│   └── {docId}/
│       ├── issueId: String
│       ├── serialNo: String
│       ├── bookName: String
│       ├── authorName: String
│       ├── membershipId: String
│       ├── issueDate: Timestamp
│       ├── returnDate: Timestamp   // scheduled return
│       ├── actualReturnDate: Timestamp (nullable)
│       ├── status: String          // active / returned / overdue
│       ├── fineCalculated: Double
│       ├── finePaid: Boolean
│       └── remarks: String
│
└── issueRequests/
    └── {docId}/
        ├── requestId: String
        ├── membershipId: String
        ├── bookName: String
        ├── requestedDate: Timestamp
        ├── fulfilledDate: Timestamp (nullable)
        └── status: String          // pending / fulfilled
```

---

## 📂 Project Structure

```
com.acxiom.librarymgmt/
│
├── activities/
│   ├── SplashActivity.java         ← Auto-login check
│   ├── LoginActivity.java          ← Dual role login
│   ├── AdminHomeActivity.java      ← 3-module admin dashboard
│   ├── UserHomeActivity.java       ← 2-module user dashboard
│   ├── ConfirmationActivity.java   ← Success screen
│   ├── CancelActivity.java         ← Cancel screen
│   └── SeedDataActivity.java       ← Test data seeder
│
├── transactions/
│   ├── TransactionsActivity.java   ← Transaction menu
│   ├── BookAvailableActivity.java  ← Search form
│   ├── SearchResultsActivity.java  ← Results with radio select
│   ├── BookIssueActivity.java      ← Issue with date rules
│   ├── ReturnBookActivity.java     ← Return with serial no
│   └── PayFineActivity.java        ← Fine calc + checkbox
│
├── reports/
│   ├── ReportsActivity.java        ← Reports menu
│   ├── MasterBooksActivity.java
│   ├── MasterMoviesActivity.java
│   ├── MasterMembershipsActivity.java
│   ├── ActiveIssuesActivity.java
│   ├── OverdueReturnsActivity.java ← Auto fine display
│   └── IssueRequestsActivity.java
│
├── maintenance/
│   ├── MaintenanceActivity.java    ← Admin-only guard
│   ├── AddMembershipActivity.java
│   ├── UpdateMembershipActivity.java
│   ├── AddBookActivity.java
│   ├── UpdateBookActivity.java
│   └── UserManagementActivity.java
│
├── models/
│   ├── User.java
│   ├── Book.java
│   ├── Membership.java
│   ├── Issue.java
│   └── IssueRequest.java
│
├── adapters/
│   ├── BookListAdapter.java        ← With radio button selection
│   ├── ReportBookAdapter.java      ← Read-only display
│   ├── IssueAdapter.java           ← Overdue row highlighting
│   └── MembershipAdapter.java      ← Inactive row greying
│
└── utils/
    ├── FirebaseHelper.java         ← Singleton Firestore wrapper
    ├── SessionManager.java         ← SharedPrefs session
    └── Constants.java              ← All app constants
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android device or emulator running API 24+
- Google account for Firebase

### Step 1 — Clone the Repository
```bash
git clone https://github.com/yourusername/LibraryManagementSystem.git
cd LibraryManagementSystem
```

### Step 2 — Firebase Setup
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project: **LibraryManagementSystem**
3. Add Android app with package: `com.acxiom.librarymgmt`
4. Download `google-services.json`
5. Place the file in the `app/` directory of the project
6. In Firebase Console → **Firestore Database** → Create database (Start in test mode)

### Step 3 — Firestore Security Rules
In Firebase Console → Firestore → Rules, paste:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true; // For development only
    }
  }
}
```
> ⚠️ Update rules for production use before deployment.

### Step 4 — Build & Run
1. Open the project in Android Studio
2. Sync Gradle: **File → Sync Project with Gradle Files**
3. Run the `SeedDataActivity` once to populate test data
4. Run the app on emulator or physical device

### Step 5 — Seed Test Data
On first launch, navigate to `SeedDataActivity` and tap **"Seed Test Data"** to create:
- Admin user (`adm` / `adm`)
- Regular user (`user` / `user`)
- 3 sample books across categories
- 1 sample membership

---

## 🔑 Login Credentials

| Role | User ID | Password | Access Level |
|------|---------|----------|-------------|
| **Admin** | `adm` | `adm` | Maintenance + Reports + Transactions |
| **User** | `user` | `user` | Reports + Transactions only |

> Passwords are masked with dots during input as per assessment requirement.

---

## 📱 Module Breakdown

### 🔄 Transactions Module

#### Book Availability Search
- Search by Book Name (dropdown) and/or Author (dropdown)
- At least one field must be filled — inline error shown on page if both empty

#### Search Results
- Tabular display: Book Name | Author | Serial No | Available | Select
- Radio button appears **only for available books**
- Single selection enforced across all rows

#### Book Issue
- Author field auto-populated from Firestore (non-editable)
- Issue Date cannot be earlier than today
- Return Date auto-set to **today + 15 days**, editable within that range

#### Return Book
- Serial No is a **mandatory field**
- Confirm **always** navigates to Pay Fine regardless of fine amount

#### Pay Fine
- Fine = `max(0, daysOverdue) × ₹5.00`
- Fine Calculated field is read-only (auto-computed)
- If fine > 0: **Fine Paid checkbox must be checked** to proceed
- If fine = 0: confirm completes transaction directly

---

### 📊 Reports Module

| Report | Query | Key Columns |
|--------|-------|-------------|
| Master List of Books | `books` where type=book | Serial No, Name, Author, Category, Status, Cost, Date |
| Master List of Movies | `books` where type=movie | Serial No, Name, Author, Category, Status, Cost, Date |
| Master List of Memberships | `memberships` | ID, Name, Contact, Aadhaar, Dates, Status, Fine |
| Active Issues | `issues` where status=active | Serial No, Book, Membership ID, Issue Date, Return Date |
| Overdue Returns | `issues` where returnDate < today | All columns + Fine Calculation |
| Issue Requests | `issueRequests` | Membership ID, Book, Requested Date, Fulfilled Date |

---

### ⚙️ Maintenance Module *(Admin Only)*

#### Membership Management
- **Add:** All fields mandatory, 6-month duration selected by default
- **Update:** Fetch by Membership Number, extend or cancel membership

#### Books / Movies Management
- **Add:** Radio toggle Book/Movie (default: Book), Category spinner, Quantity defaults to 1
- **Update:** Update status (available / issued / damaged / lost)

#### User Management
- Radio toggle New User (default) / Existing User
- **Active checkbox:** unchecked = deactivated account
- **Admin checkbox:** unchecked = regular user role

---

## ✅ Business Rules & Validations

| Screen | Validation Rule |
|--------|----------------|
| Login | Both User ID and Password required; password hidden |
| Book Available | At least one field (Book Name OR Author) required |
| Book Issue | Book Name required; Issue Date ≥ today; Return Date max +15 days |
| Return Book | Book Name + Serial No are mandatory |
| Pay Fine | Fine Paid checkbox must be checked if fine > 0 |
| Add Membership | All fields mandatory; default duration = 6 months |
| Update Membership | Membership Number mandatory to fetch details |
| Add Book/Movie | All fields mandatory; Quantity defaults to 1; default type = Book |
| Update Book/Movie | All fields mandatory; default type = Book |
| User Management | Name is mandatory; default selection = New User |
| Maintenance Access | Admin role check at every entry point |

---

## 📚 Book Category Codes

| Category | Code Prefix | Book Serial Format | Movie Serial Format |
|----------|------------|-------------------|-------------------|
| Science | SC | SC-B-000001 | SC-M-000001 |
| Economics | EC | EC-B-000001 | EC-M-000001 |
| Fiction | FC | FC-B-000001 | FC-M-000001 |
| Children | CH | CH-B-000001 | CH-M-000001 |
| Personal Development | PD | PD-B-000001 | PD-M-000001 |

---

## 📸 Screenshots

| Login Screen | Admin Home | User Home |
|:---:|:---:|:---:|
| ![Login](https://github.com/user-attachments/assets/e70a794d-55bb-4953-a30c-43de09df064c) | ![Admin](https://github.com/user-attachments/assets/35878d90-cdb2-46cd-bd48-12a2c404a17b) | ![User](https://github.com/user-attachments/assets/30decfc7-3156-4b28-aff1-9c8f91188981) |

| Book Search | Search Results | Book Issue |
|:---:|:---:|:---:|
| ![Search](https://github.com/user-attachments/assets/ae303112-5cd9-47d8-8dcf-00df62f8e329) | ![Results](https://github.com/user-attachments/assets/4416b7fa-8957-4704-95d5-9352ed3cd238) | ![Issue](https://github.com/user-attachments/assets/c761fa8a-2586-46c6-baed-01f9695a31e4) |

| Return Book | Pay Fine | Reports |
|:---:|:---:|:---:|
| ![Return](https://github.com/user-attachments/assets/c8872b23-5e82-4d53-a980-e0eb94dda3f2) | ![Fine](https://github.com/user-attachments/assets/bb29858d-12cc-46e3-9429-273961df24d9) | ![Reports](https://github.com/user-attachments/assets/3ecd4f52-8962-48b3-a680-db55f6888e58) |

| Add Membership | Add Book | User Management |
|:---:|:---:|:---:|
| ![Membership](https://github.com/user-attachments/assets/8ef8b2f5-40fc-45c0-a4c3-850e31df4c22) | ![Book](https://github.com/user-attachments/assets/0524ad11-300b-4c9d-8f5e-5551a881d66c) | ![Users](https://github.com/user-attachments/assets/b9a452e0-1953-4ba5-b353-2e64e97c681f) |

---

## ✔️ Assessment Requirements Coverage

| Requirement (from Instructions Sheet) | Status |
|---------------------------------------|:------:|
| 2 types of login — Admin and User | ✅ |
| Admin has access to Maintenance, Reports, Transactions | ✅ |
| User cannot access Maintenance | ✅ |
| Passwords hidden on login screens | ✅ |
| Maintenance module built to feed Reports and Transactions | ✅ |
| Book Available — at least one field required with inline error | ✅ |
| Search Results — radio button for available books only | ✅ |
| Book Issue — Book Name is required | ✅ |
| Book Issue — Author auto-populated and non-editable | ✅ |
| Book Issue — Issue Date cannot be less than today | ✅ |
| Book Issue — Return Date auto = today+15, max 15 days | ✅ |
| Book Issue — Remarks is non-mandatory | ✅ |
| Return Book — Book Name required | ✅ |
| Return Book — Author auto-populated and non-editable | ✅ |
| Return Book — Serial No is mandatory | ✅ |
| Return Book — Issue Date auto-populated and non-editable | ✅ |
| Return Book — Confirm always goes to Pay Fine | ✅ |
| Pay Fine — all fields pre-populated except Fine Paid and Remarks | ✅ |
| Pay Fine — Fine Paid checkbox required if fine > 0 | ✅ |
| Add Membership — all fields mandatory | ✅ |
| Add Membership — 6 months selected by default | ✅ |
| Update Membership — Membership Number mandatory to populate fields | ✅ |
| Update Membership — 6 months extension default | ✅ |
| Add Book — Book default selected | ✅ |
| Add Book — all fields mandatory | ✅ |
| Update Book — Book default selected | ✅ |
| Update Book — all fields mandatory | ✅ |
| User Management — New User default selected | ✅ |
| User Management — Name mandatory | ✅ |

**Total: 29 / 29 Requirements Covered ✅**

---

## 👨‍💻 Author

**[Your Full Name]**
Android Developer | Acxiom Consulting Assessment Candidate

- 📧 Email: rohitnagartech124@gmail.com | en24ca5030142@medicaps.ac.in
- 💼 LinkedIn: https://www.linkedin.com/in/rohitnagartech/
- 🐙 GitHub: (https://github.com/rohitnagartech)

---

## 📄 License

This project was developed exclusively for the **Acxiom Consulting Technical Assessment**.
All rights reserved © 2025.

---

<p align="center">
  Built with ❤️ using Android Studio + Firebase
  <br/>
  <strong>Library Management System - Acxiom Consulting Assessment, ROHIT NAGAR</strong>
</p>
