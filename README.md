# 📸 PickPic  

PickPic is a mobile and web application for capturing, organizing, and sharing event photos. Whether it’s a wedding, birthday party, or corporate gathering, PickPic allows organizers and guests to collaborate on shared albums, upload and rank photos, and download curated photo sets — making event photo management effortless and fun. 

Made by [Jordan Jamali](https://github.com/JJamali), [Eugene Bosca](https://github.com/eugene-bosca), [LuDa Yu](https://github.com/ludayu560), [Alan Chen](https://github.com/AlanWYChen), [Sam Zhu](https://github.com/theasdfone), and [Ethan Dobrowolski](https://github.com/edobrowo)

---

## 🚀 Features  

- **Event Management**  
  - Create and manage events with unique QR codes for invitations.  
  - Add/remove guests and photos, or delete entire albums.  

- **Photo Sharing & Ranking**  
  - Guests upload event photos and vote on their favorites.  
  - Ranked photos are highlighted with gold borders.  
  - Sort photos by date or ranking.  

- **Convenient Access**  
  - Upload and download directly from device photo apps.  
  - Download curated sets (Top 10, Top 20, or custom selections).  
  - Web integration for view-only access without installing the app.  

- **User Experience Enhancements**  
  - Smooth navigation and minimalist design for accessibility.  
  - QR code and invite link workflows reduce setup time to <15s.  
  - Optimized caching system for faster image loading.  

---

## 🛠️ Tech Stack  

- **Frontend (Mobile)**: Kotlin, Jetpack Compose, MVVM, Hilt  
- **Frontend (Web)**: React (view-only mode)  
- **Backend**: Django REST Framework, Google Cloud Run  
- **Database**: Google Cloud SQL (MySQL)  
- **Auth**: Firebase Authentication, Google Credential Manager  
- **Infra**: Google Cloud Platform  

---

## 📐 Architecture  

### Mobile (MVVM)  
- **View**: Jetpack Compose screens for event management, photo upload, ranking, etc.  
- **ViewModel**: State management using LiveData/StateFlow, injected via Hilt.  
- **Model**: Repositories abstract data sources and services, ensuring a single source of truth.  

### Server–Client  
- **Frontend** communicates with the backend via `EventApiService` and `UserApiService`.  
- **Backend** handles business logic, file storage, and database access.  
- **Scalability**: Cloud infrastructure allows simultaneous large-scale event usage.  

### Design Patterns  
- **Facade**: Simplified repository interfaces to manage multiple subsystems (auth, events, users, images).  
- **Proxy**: Implemented caching to reduce image load latency by checking cache before making network requests.  

---

## 🔒 Non-Functional Requirements  

- **Usability & Accessibility**:  
  - Invite/join events via QR codes and links.  
  - Fallback web view for non-Android users.  
  - Minimalist design with intuitive navigation.  

- **Efficiency**:  
  - Proxy pattern for cached image loading.  
  - Optimized APIs separating metadata from image data.  

- **Privacy & Security**:  
  - Secure authentication via Firebase + Google Credential Manager.  
  - Backend authorization with Bearer tokens.  

- **Portability**:  
  - Web integration provides cross-platform access.  

---

## 📊 User Testing  

Average completion times for core workflows:  

| Person   | Create account + accept invite (s) | Create event + add photo + invite (s) |  
|----------|-------------------------------------|---------------------------------------|  
| Tian     | 10                                  | 33                                    |  
| Shrey    | 24                                  | 47                                    |  
| Morgan   | 16                                  | 25                                    |  
| Avesta   | 11                                  | 21                                    |  
| Aksel    | 14                                  | 29                                    |  

✅ Average times were well below the 15s per-task usability target.  

---

## 📸 Screenshots & Diagrams  

- [Class Diagram](https://github.com/JJamali/team04-BMExCS/blob/main/pick_pic_class_diagram.svg)  
- Deployment Diagram (Server–Client architecture)  

<p align="center">
  <img src="https://github.com/user-attachments/assets/c4ff6300-17d4-45a2-af6e-3f52e2153d76" width="45%" />
</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/20458b17-6080-4359-9997-efe0e969a64a" width="45%" />
  <img src="https://github.com/user-attachments/assets/feb94afb-757b-4dce-a31b-64abceed4f3a" width="45%" />
</p>
---

## 📂 Repository Structure  

/frontend/app/src/main/java/com/bmexcs/pickpic/
├── presentation/screens/       # Jetpack Compose screens
├── presentation/viewmodels/    # ViewModels
├── data/repositories/          # Repositories (Facade pattern)
├── data/sources/               # Data sources
└── data/services/              # API + Auth services

/backend/                       # Django REST Framework backend
/web/                           # Web integration (view-only)


## 👥 Development Process  

- **Iteration 1**: Infrastructure setup, MVVM patterns, ranking logic, Firebase Auth prototype.  
- **Iteration 2**: Backend (Django + GCP) setup, API integration, improved coding standards, Google Credential Manager refactor.  
- **Iteration 3**: QR codes, batch uploading, UI improvements, app icon.  
- **Final Demo**: Efficiency optimizations, caching, web page integration, particle animations, bug fixes, UI polish.  

Total commits: **767**, reflecting incremental, collaborative progress.  
