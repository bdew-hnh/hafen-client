### Main features:

* Automated mapping, including sticher that can merge maps across sessions
* Improved fonts and sizes
* Larger minimap
* Completion percent text on items (e.g. in study window)
* Option to always show the long tooltip on items
* Option to show boulders on minimap
* Option to show plant and tree growth stages
* Option to show damage on objects
* Option to show all players on minimap (taken from romovs client)
* Option to show arrows on minimap (for those tired loosing them when hunting)
* Option to hide flavour objects - reduces CPU utilization (taken from romovs client)
* Daylight mode - Ctrl+N to toggle (taken from k-t client)
* Player arrow on minimap (taken from k-t client)
* Grid overlay (somewhat based on k-t client)
* Holding down LMB will make character walk in the direction of the cursor
* Mouse follow mode - press Ctrl+F for your character to follow the cursor
* Drop/Transfer identical items (taken from romovs client)
* Item QL display on icon (from enders)
* Kin online/offline message (from enders)

### Keys
* CTRL + N - Night vision toggle
* CTRL + W - World tooltip toggle
* CTRL + F - Follow mouse
* CTRL + G - Grid overlay
* CTRL + ALT + Left Click - Drop identical items
* CTRL + Right Click - Transfer identical items between inventory and container

### Running

* To run the client on Windows double click **run.bat** or **run-debug.bat**.
* On other OS: 
  
  ```java -Xms512m -Xmx1024m -jar hafen.jar -U http://game.havenandhearth.com/hres/ game.havenandhearth.com```

* To run map stitcher on Windows double click combiner.bat 
* On other OS: ```java -jar combiner.jar```

### Links

* [H&H forum thread](http://www.havenandhearth.com/forum/viewtopic.php?f=49&t=40945)
