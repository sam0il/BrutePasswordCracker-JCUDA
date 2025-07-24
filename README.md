# BrutePasswordCracker-JCuda

[![Windows](https://img.shields.io/badge/Windows-10%2F11-blue?logo=windows)](https://www.microsoft.com/windows/)
[![Linux](https://img.shields.io/badge/Linux-Ubuntu%2022.04-orange?logo=linux)](https://ubuntu.com/)
[![CUDA](https://img.shields.io/badge/CUDA-11.8-green?logo=nvidia)](https://developer.nvidia.com/cuda-toolkit)

---

## 🌟 Features
- **Brute-force password cracking using NVIDIA GPUs**  
- **Multi-platform support:** Windows 10/11 and Linux (Ubuntu 22.04 tested)  
- **Dictionary and mask-based attack modes**  
- **Optimized CUDA kernels for maximum performance**  

---

## 📦 Requirements

### Both Platforms
| Component | Requirement |
|-----------|-------------|
| **GPU**   | NVIDIA GPU (Compute Capability 3.0+) |
| **Java**  | JDK 17+ (Adoptium recommended) |
| **CUDA**  | Toolkit 11.8 |

### Windows-Specific
- Visual Studio 2019 Build Tools  
- Windows 10 SDK (10.0.19041+)  
- VC++ Redistributable 2019  

### Linux-Specific
- `bash`  
- Build tools:  
  ```bash
  sudo apt install build-essential freeglut3-dev libglfw3-dev libgles2-mesa-dev
🚀 Installation Guide
Windows Setup
1. Install Dependencies
Download and install CUDA Toolkit 11.8

Install Visual Studio 2019 Build Tools

Install Java 17 JDK (Adoptium recommended)

2. Clone the Repository
powershell
Copy
Edit
git clone https://github.com/sam0il/BrutePasswordCracker-JCUDA.git
cd BrutePasswordCracker-JCUDA
3. Add JCuda Libraries in IntelliJ
Right-click the lib folder → "Add as Library"

Select the following JAR files:

jcuda-11.8.0.jar

jcuda-natives-11.8.0-windows-x86_64.jar

Set the scope to Compile and Runtime

4. Set Native Library Path
Go to Run > Edit Configurations...

Add the VM option:

ini
Copy
Edit
-Djava.library.path=native
Linux Setup
1. Install Dependencies
bash
Copy
Edit
sudo apt update
sudo apt install openjdk-17-jdk nvidia-cuda-toolkit git
2. Clone the Repository
bash
Copy
Edit
git clone https://github.com/sam0il/BrutePasswordCracker-JCUDA.git
cd BrutePasswordCracker-JCUDA
3. Replace Windows Files with Linux Equivalents
Download Linux natives from JCuda Downloads

Replace files:

bash
Copy
Edit
# Remove Windows-specific files
rm lib/jcuda-natives-11.8.0-windows-x86_64.jar
rm native/*.dll

# Add Linux natives
cp ~/Downloads/jcuda-natives-11.8.0-linux-x86_64.jar lib/
cp /usr/local/cuda-11.8/lib64/lib*.so native/
▶️ Running the Application
Windows
IntelliJ:
Open src/main/java/org/example/Main.java

Run with VM option:

ini
Copy
Edit
-Djava.library.path=native
Command Line:
powershell
Copy
Edit
java -cp "lib/*;src/main/java" -Djava.library.path=native org.example.Main
Linux
Set Library Path:
bash
Copy
Edit
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/cuda-11.8/lib64:$PWD/native
Run Application:
bash
Copy
Edit
java -cp "lib/*:src/main/java" -Djava.library.path=native org.example.Main
🔧 Path Configuration Notes
Windows: Use -Djava.library.path=native to point to .dll files.

Linux:

Set LD_LIBRARY_PATH to include both CUDA and the native directory.

Use Linux-specific natives downloaded from the JCuda website.
