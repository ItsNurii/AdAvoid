# 🎬 AdVoid

AdVoid is a Java Swing desktop application that provides a modern and intuitive interface for downloading, managing, and processing multimedia content without ads, using **yt-dlp** and **FFmpeg** as its core engines.

The application combines media downloading, file management, user authentication, and UI customization (light/dark mode) into a single, cohesive desktop experience.

---

## 📘 About the Project

AdVoid was developed as part of a **Java Desktop Application project** for the *Development of Multiplatform Applications (DAM)* program.

The main goal of the project is to demonstrate:

- Advanced use of Java Swing
- Background task management
- File system interaction
- UI theming (Light / Dark mode)
- Modular, panel-based application design

---

## ✨ Core Features

### 🔐 Login System
- Email & password authentication via API
- Remember Me option for persistent login
- Auto-login using stored token
- Secure logout with credential cleanup
- Dynamic panel switching (Login ↔ Main App)

### ⬇️ Media Downloading
- Download video or audio from supported platforms using yt-dlp
- Automatic post-processing and conversion with FFmpeg
- Background execution to keep the UI responsive
- Real-time progress tracking with a dynamic progress bar

### 🎨 User Interface
- Built entirely with Java Swing
- Clean, modern, and responsive layout
- Light mode (default) and Dark mode
  - Light mode → light blue theme
  - Dark mode → light gray theme
- Styled buttons, labels, and tables
- Custom icons in the Login screen (email & password icons)

### 📊 Media Management – Details Panel
AdVoid includes a **Details Panel** that turns the app into a small media manager.

**Functionalities:**
- Automatic scan of the download folder
- Supported formats: `.mp3`, `.mp4`, `.mkv`, `.webm`
- File visualization using:
  - `JList` for quick browsing
  - `JTable` with a custom `AbstractTableModel`
- Displayed file information:
  - File name
  - Size
  - Last modified date
- Format filtering via `JComboBox`
- Refresh button to rescan the folder
- Delete selected files directly from the UI
- Return button to navigate back to the main panel

---

## 🧩 Technologies Used

| Tool / Library | Purpose |
|----------------|---------|
| Java (Swing)   | GUI framework |
| yt-dlp         | Video and audio downloader |
| FFmpeg         | Media conversion and post-processing |
| SwingWorker    | Background task execution |
| Maven          | Build and dependency management |
| Custom Images  | App branding and icons |

---

## ⚙️ Installation & Setup

Clone the repository:

```bash
git clone https://github.com/yourusername/AdVoid.git
cd AdVoid
```

Make sure yt-dlp and FFmpeg are installed and accessible from the system:
```bash
yt-dlp --version
ffmpeg -version
```
Build and run the project (Maven or IDE):
```bash
mvn clean compile exec:java
```

### 🚀 Usage
- Launch the application
- Log in using your email and password
- Paste a media URL into the input field
- Choose format and settings from the Edit Panel
- Click Download
- Track progress in real time
- Manage downloaded files from the Details Panel
- Switch between Light Mode and Dark Mode from the menu
  - Light Mode → light theme
  - Dark Mode → dark theme

### 🔒 Security & Local Storage
- Session tokens are stored locally in: `~/.advoid/token.txt`
- Credentials are saved only if **Remember Me** is enabled
- Tokens and credentials are removed on logout
- Custom icons and images are used under fair use or free licenses

### 🎨 UX & Usability Documentation
This section documents UX and usability improvements, justifying design and implementation decisions at both interface and code level.

#### 1️⃣ Aspect, Color, Icons, Text & Component Distribution

**Color & Theme**  
- Light Mode (default): light blue palette  
- Dark Mode: light gray background with white text  
- Implementation:  
  - Colors centralized in an `AppColor` utility class  
  - Each panel implements `applyTheme(boolean darkMode)`  
  - Avoids duplicated code and simplifies maintenance  

**Icons**  
- Email and password labels replaced with icons  
- Improves visual clarity and modern appearance  
- Icons loaded from `src/main/resources/images` using `getResource()`  

**Text**  
- Clear, short, user-oriented messages  
- Action-based button labels (Download, Refresh, Delete)  
- Error messages written in plain language  

**Component Distribution**  
- Layout managers (`GridBagLayout`, `BorderLayout`, `FlowLayout`) are used  
- Logical top-to-bottom workflow  
- Consistent spacing improves visual hierarchy and scalability  

#### 2️⃣ Affordance, Feedback & Restrictions

**Affordance**  
- Buttons visually indicate clickability  
- Icons suggest purpose  
- Tables and lists indicate selectable elements  

**Feedback**  
- Real-time progress bar during downloads  
- Status messages and dialogs inform the user of actions and errors  
- Confirmation dialogs for destructive actions (file deletion)  

**Restrictions**  
- Download/Login buttons disabled if required fields are empty  
- Actions requiring authentication blocked when not logged in  
- File deletion requires selection  

#### 3️⃣ Other Usability Improvements
- Remember Me and Auto-login improve UX  
- Dynamic panel switching avoids window clutter  
- Details Panel enables file management without leaving the app  
- Format filtering speeds up file searching  
- Dark mode JTable customization ensures column headers remain readable  

#### 4️⃣ Error Handling, Exceptions & User Feedback
- Critical operations wrapped in `try-catch` blocks  
- User-friendly messages via `JOptionPane` or status labels  
- Application avoids crashes whenever possible  
- Critical errors logged using `java.util.logging.Logger`  

#### 5️⃣ Clean Code, Naming & Project Structure
- Meaningful method and variable names  
- Centralized theme management  
- Clear package separation (`login`, `panels`, `use`)  
- Resources stored in `src/main/resources`  
- Atomic commits with descriptive messages  
- README fully documents UX and technical decisions  

### 👩‍💻 Author
**Nuria** – 📚 Development of Multiplatform Applications (DAM)  
🎓 Academic Java Desktop Project  

### 📝 Credits & Resources
- `yt-dlp` — video/audio downloader  
- `FFmpeg` — multimedia framework  
- `Java Swing` — desktop UI framework  
- Custom icons and images used under fair use or free licenses  

### 📄 License
This project is released under the **MIT License**. You are free to use, modify, and distribute it with proper attribution.
