BrutePasswordCracker-JCuda
https://img.shields.io/badge/Windows-10%252F11-blue?logo=windows
https://img.shields.io/badge/Linux-Ubuntu%252022.04-orange?logo=linux
https://img.shields.io/badge/CUDA-11.8-green?logo=nvidia

🌟 Features
Brute-force password cracking using NVIDIA GPUs

Multi-platform support (Windows 10/11, Linux)

Dictionary and mask-based attack modes

Optimized CUDA kernels for maximum performance

📦 Requirements
Both Platforms
Component	Requirement
GPU	NVIDIA GPU (Compute Capability 3.0+)
Java	JDK 17+ (Adoptium)
CUDA	Toolkit 11.8 (Download)
Windows-Specific
Visual Studio 2019 Build Tools

Windows 10 SDK (10.0.19041+)

VC++ Redistributable 2019

Linux-Specific
bash
sudo apt install build-essential freeglut3-dev libglfw3-dev libgles2-mesa-dev  
🚀 Installation Guide
Windows Setup
Install dependencies:

CUDA Toolkit 11.8

VS 2019 Build Tools

Java 17 JDK

Clone repository:

powershell
git clone https://github.com/sam0il/BrutePasswordCracker-JCUDA.git  
cd BrutePasswordCracker-JCUDA  
Add JCuda libraries in IntelliJ:

Right-click lib folder → "Add as Library"

Select both JAR files:

jcuda-11.8.0.jar

jcuda-natives-11.8.0-windows-x86_64.jar

Set scope to "Compile and Runtime"

Set native library path:

Go to Run > Edit Configurations...

Add VM option: -Djava.library.path=native

Linux Setup
Install dependencies:

bash
sudo apt update  
sudo apt install openjdk-17-jdk nvidia-cuda-toolkit git  
Clone repository:

bash
git clone https://github.com/sam0il/BrutePasswordCracker-JCUDA.git  
cd BrutePasswordCracker-JCUDA  
Replace Windows files with Linux equivalents:

Download Linux natives from JCuda Downloads

Replace files:

bash
# Remove Windows-specific files  
rm lib/jcuda-natives-11.8.0-windows-x86_64.jar  
rm native/*.dll  

# Add Linux natives  
cp ~/Downloads/jcuda-natives-11.8.0-linux-x86_64.jar lib/  
cp /usr/local/cuda-11.8/lib64/lib*.so native/  
▶️ Running the Application
Windows
In IntelliJ:

Open src/main/java/org/example/Main.java

Run with VM option: -Djava.library.path=native

Command Line:

powershell
java -cp "lib/*;src/main/java" -Djava.library.path=native org.example.Main  
Linux
Set library path:

bash
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/cuda-11.8/lib64:$PWD/native  
Run application:

bash
java -cp "lib/*:src/main/java" -Djava.library.path=native org.example.Main  
📂 Project Structure (Keep Existing)
text
BrutePasswordCrackerJCuda/  
├── lib/                          # JCuda libraries  
│   ├── jcuda-11.8.0.jar          # Core JCuda (cross-platform)  
│   └── [platform-specific jar]   # Windows or Linux natives  
├── native/                       # Compiled binaries  
│   ├── *.dll (Windows)           # OR  
│   └── *.so (Linux)              # Platform-specific binaries  
└── src/  
    └── main/java/org/example/    # Source code  
        ├── HashValidator.java    # Hash validation  
        ├── JCudaBruteForceEngine.java # Brute-force engine  
        ├── JCudaDictionaryEngine.java # Dictionary engine  
        ├── JCudaKernel.cu        # CUDA kernel source  
        ├── JCudaKernel.ptx       # Precompiled kernel  
        ├── Main.java             # Entry point  
        └── MaskConfig.java       # Mask configuration  
⚠️ Troubleshooting
Common Issues
Platform	Symptom	Solution
Both	No CUDA devices found	Update NVIDIA drivers
Run nvidia-smi to verify GPU
Windows	UnsatisfiedLinkError	1. Install VC++ Redist 2019
2. Set -Djava.library.path=native
3. Reboot
Linux	libcudart.so.11.8 not found	sudo ldconfig /usr/local/cuda-11.8/lib64
Linux	GLIBCXX not found	sudo apt install libstdc++6
Verify Installation
Windows:

powershell
nvcc --version  # Should show 11.8  
nvidia-smi      # Check GPU detection  
java -version   # Should show 17+  
Linux:

bash
nvcc --version  
nvidia-smi  
java -version  
🔧 Path Configuration Notes
Windows: Use -Djava.library.path=native to point to DLLs

Linux: Two critical steps:

Set LD_LIBRARY_PATH to include CUDA and native directory

Use Linux-specific natives from JCuda website

Same Project Structure: No reorganization needed - just replace platform-specific files
