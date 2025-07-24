# GPU Password Cracker with JCuda
[![Windows Support](https://img.shields.io/badge/Windows-10%2F11-blue?logo=windows)](https://github.com/sam0il/BrutePasswordCracker-JCUDA)
[![Linux Support](https://img.shields.io/badge/Linux-Ubuntu%2022.04-orange?logo=linux)](https://github.com/sam0il/BrutePasswordCracker-JCUDA)
[![CUDA Required](https://img.shields.io/badge/CUDA-11.8-green?logo=nvidia)](https://developer.nvidia.com/cuda-toolkit-archive)

## 🌟 Features
- Brute-force password cracking using NVIDIA GPUs
- Multi-platform support (Windows 10/11, Linux)
- Dictionary and mask-based attack modes
- Optimized CUDA kernels for maximum performance

## 📦 Requirements
### Both Platforms
| Component | Requirement |
|-----------|-------------|
| **GPU** | NVIDIA GPU (Compute Capability 3.0+) |
| **Java** | JDK 17+ ([Adoptium](https://adoptium.net/)) |
| **CUDA** | Toolkit 11.8 ([Download](https://developer.nvidia.com/cuda-11-8-0-download-archive)) |

### Windows-Specific
- Visual Studio 2019 Build Tools
- Windows 10 SDK (10.0.19041+)
- [VC++ Redistributable 2019](https://aka.ms/vs/16/release/vc_redist.x64.exe)

### Linux-Specific
```bash
sudo apt install build-essential freeglut3-dev libglfw3-dev libgles2-mesa-dev
