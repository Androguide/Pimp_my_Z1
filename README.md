Pimp My Z1
==========

Pimp My Z1 is a sleek, holo, fully Open-Source (GPL v2 license) app which allows to control various sysfs interfaces present on some kernels for the Z1.
Most features require either [Pimped Kernel](http://forum.xda-developers.com/showthread.php?t=2660679) (for CM-based roms) or [Doom Kernel](http://forum.xda-developers.com/showthread.php?t=2448613) (for Stock-based roms), but the CPU & GPU Control as well as Touch Screen control will work properly with any kernel including the stock one.


### Features

- __CPU Control__: 
  - Set your minimum & maximum CPU frequencies (and overclock if your kernel allows it)
  - Set a different Governor for each CPU core
  - View your current CPU frequency in real-time and with a graph
  - Select your TCP Congestion Algorithm
  - Toggle thermal throttling (msm_thermal) on/off
  - Toggle Snake Charmer on/off


- __GPU Control__: 

  - Set your minimum & maximum GPU frequencies (and overclock it if your kernel allows it)
  - Select your GPU Governor
  - View your current GPU frequency in real-time and with a graph


- __Color Calibration__:
  - Support for LCD_KCAL interface, allowing to control the RGB channels of the display. 


- __Power Management__: 

  - Select your level of multi-core power savings, a.k.a sched_mc power savings (disabled/moderate/aggressive)
  - Select your hotplug driver (MPDecision/Intelliplug/Alucard Hotplug)
  - Toggle IntelliPlug's eco mode on and off
  - Set IntelliPlug's eco mode max amount of online cores
  - Set Alucard Hotplug's max amount of online cores
  - Toggle between the default early_suspend PM driver and the newer power_suspend PM driver


- __I/O Tweaks__:

  - Toggle Dynamic File Sync on and off
  - Select your I/O Scheduler independently for the internal & external storage
  - Toggle eMMC & SD-Card entropy contribution independently


- __Memory Management__:

  - Toggle Kernel Same-page Merging (KSM) on and off
  - Set KSM's amount of pages to scan
  - Set KSM's timer in milliseconds


- __Miscellaneous__:
  - Set the vibrator intensity at kernel level
  - Set Fast Charge mode (off|force A/C|manual)
  - Set FastCharge amperage in manual mode


- __Sound Control__:

  - Independently control the digital headphone output left/right channels gain at a hardware level
  - Independently control the analog headphone output left/right channels gain at a hardware level
  - Independently control the speaker output left/right channels gain at a hardware level 
  - Control the microphone input gain at a hardware level
  - Control the camera microphone input gain at a hardware level


- __Touch Screen Control__:

  - Enable/Disable Pen Mode to allow/disallow the use of any non-plastic pen or object as a stylus for the touchscreen
  - Enable/Disable Glove Mode to allow/disallow using the touchscreen while wearing gloves
  - Enable/Disable Double-Tap 2 Wake



### Screenshots
![Pimpalicious Screenshot](http://i.imgur.com/SWmf3yh.png) - ![Pimpalicious Screenshot](http://i.imgur.com/zkelHsZ.png) ![Pimpalicious Screenshot](http://i.imgur.com/tSygF8R.png) - ![Pimpalicious Screenshot](http://i.imgur.com/ZZLZApg.png) ![Pimpalicious Screenshot](http://i.imgur.com/na7yUtw.png) - ![Pimpalicious Screenshot](http://i.imgur.com/XdEF3Ex.png) ![Pimpalicious Screenshot](http://i.imgur.com/mISVpbe.png) - ![Pimpalicious Screenshot](http://i.imgur.com/lEktpra.png) ![Pimpalicious Screenshot](http://i.imgur.com/qqfnmEv.png) - ![Pimpalicious Screenshot](http://i.imgur.com/3uyzeAq.png) ![Pimpalicious Screenshot](http://i.imgur.com/pJoyfo2.png) - ![Pimpalicious Screenshot](http://i.imgur.com/RrmYZT9.png) ![Pimpalicious Screenshot](http://i.imgur.com/8bDUYiU.png)
