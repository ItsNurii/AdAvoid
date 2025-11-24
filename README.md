# 🎬 AdVoid

A desktop application built in **Java Swing** that provides an intuitive interface for downloading and managing video or audio content using the **yt-dlp** and **FFmpeg** tools.

---

## 📘 About the Project

**AdVoid** was developed as part of a Java desktop application project for the **[Your Course Name / Subject]** at **[Your Institution]**.

The app allows users to:
- Download media from online platforms via `yt-dlp`.
- Process or convert downloaded media using `ffmpeg`.
- Manage and edit download preferences in a clean and modern GUI.
- Track download progress visually with a dynamic progress bar.
- Access application information in a styled “About” dialog.

---

## 🖥️ Features

✅ Simple and user-friendly interface built with **Swing**.  
✅ Supports background download execution using **yt-dlp**.  
✅ Automatic conversion and post-processing with **FFmpeg**.  
✅ Real-time **progress tracking** with a light purple progress bar.  
✅ Multiple panels for editing, configuration, and about information.  
✅ Cross-platform (Windows, macOS, Linux).  

---

## 🧩 Technologies Used

| Tool / Library | Purpose |
|-----------------|----------|
| **Java (Swing)** | GUI framework |
| **yt-dlp** | Video and audio downloader |
| **FFmpeg** | Media processing and conversion |
| **Images / Logo** | Custom app branding |
| **Maven** | Build and dependency management |

---

## ⚙️ Installation and Setup

1. **Clone this repository**:
   ```bash
   git clone https://github.com/yourusername/AdVoid.git
   cd AdVoid
Make sure yt-dlp and ffmpeg are installed on your system and accessible from the terminal:

bash
Copiar código
yt-dlp --version
ffmpeg -version
Build and run the project (using Maven or your IDE):

bash
Copiar código
mvn clean compile exec:java
🚀 Usage
Launch the app (AdVoid main window).

Paste a video URL in the input field.

Choose your preferred format or settings in the Edit Panel.

Click Download to start.

Track your progress with the purple progress bar.

Access the About section for info on authorship and credits.

👨‍💻 Author
[Your Name]
📚 [Your Course / Year / Institution]
📧 [Your Email if you want]

📝 Credits and Resources
This project makes use of the following open-source tools and resources:

🎥 yt-dlp — video/audio downloader

🎧 FFmpeg — multimedia framework for conversion and post-processing

🖼️ Custom icons and logos used under fair use or free license

☕ Built with Java Swing and Maven

📄 License
This project is released under the MIT License.
You are free to use, modify, and distribute this software as long as proper credit is given.


🧱 Part 2 – Media Management & Details Panel

In Part 2, AdVoid evolves into a complete media management tool, adding a new Details Panel and improved GUI navigation.

🔍 New Functionalities

🗂 DetailsPanel Integration – A new panel where users can view and manage all downloaded media files.

🎞 Automatic Media Detection – The panel scans the download folder and lists files with extensions:
.mp3, .mp4, .mkv, and .webm.

🧠 Dynamic Filtering – Users can select a format from a JComboBox to display only files of that type (or all).

📋 Media Table View – Uses a JTable with a custom AbstractTableModel (MediaTableModel) to display file details such as name, size, and last modified date.

🧾 File List Display – Includes a JList for quick browsing of file names.

🔄 Interactive Refresh – The Refresh button updates both the list and the table with the current folder contents.

🧹 Delete Option – Users can select and delete files directly from the list or table.

🔙 Return Navigation – The Return button allows going back to the main application panel.

🪄 Main JFrame Connection – The panel is opened by clicking the jButtonDetails button from the main window.

⚙️ How It Works

When the user opens the Details Panel, the application scans the download folder for supported file types.

The list (JList) and table (JTable) are populated using the data found.

Selecting a filter in the combo box (e.g., “MP3”) updates the list and table to show only .mp3 files.

Clicking Refresh rescans the folder.

Delete removes the selected file(s) from the system.

Return navigates back to the main window.

💾 Supported Formats

.mp3 (Audio)

.mp4 (Video)

.mkv (Video)

.webm (Video)

📌 Login System – Java Swing

This project implements a Login System in Java Swing with support for:

✔ Email + Password authentication
✔ “Remember Me” persistent login
✔ Auto-login on startup
✔ Secure logout behavior
✔ Dynamic panel switching (Login → Main Window → Login)
