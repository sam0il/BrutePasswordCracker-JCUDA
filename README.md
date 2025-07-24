## Requirements for Running on Windows

This project requires specific dependencies to run correctly. Please ensure you meet all requirements before attempting to run:

### 🖥️ System Requirements
1. **Windows 10** (64-bit only)
2. **NVIDIA GPU** with CUDA support (Compute Capability 3.0+)
3. **Latest NVIDIA Drivers** (tested with 535+)

### 📦 Mandatory Dependencies
1. **CUDA Toolkit 11.8**  
   [Download installer](https://developer.nvidia.com/cuda-11-8-0-download-archive)  
   *During installation:*
   - Select "Custom" installation
   - Ensure "CUDA Runtime", "Development Tools", and "Documentation" are selected
   - Add CUDA to system PATH when prompted

2. **Visual Studio 2019 Build Tools**  
   [Download installer](https://aka.ms/vs/16/release/vs_BuildTools.exe)  
   *Required components:*
   - C++ CMake tools for Windows
   - MSVC v142 - VS 2019 C++ x64/x86 build tools
   - Windows 10 SDK (10.0.19041.0 or later)


### ⚙️ Post-Installation Checks
Verify your setup with these commands in PowerShell/CMD:
```bash
# Check CUDA installation
nvcc --version  # Should show 11.8

# Check NVIDIA drivers
nvidia-smi  # Should show driver version and GPU details

# Check Visual Studio tools
cl  # Should show Microsoft C/C++ compiler info
