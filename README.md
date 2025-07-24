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
