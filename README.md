### Main features:

* Automated mapping, including sticher that can merge maps across sessions
* Improved fonts and sizes
* Improved minimap (see below)
* Completion percent text on items (e.g. in study window)
* Transfer by highest/lowest QL with mousewheel
* Nightvision mode (with configurable brightness)
* Hide item from cursor
  * Allows limited interaction with the game world while holding an item - walking, opening containers, etc.
* Option to always show the long tooltip on items
* Option to show plant and tree growth stages
* Option to show damage on objects
* Option to selectively hide trees, plants, fences and houses
* Option to hide flavour objects - reduces CPU utilization (taken from romovs client)
* Option to disable dynamic lights
* Option to show object (mine supports, etc.) radius (based on romovs client)
* Orthogonal camera lock mode option (normal, 0/90/180/270, 8 way, free)
* Daylight mode - Ctrl+N to toggle (taken from k-t client)
* Grid overlay (somewhat based on k-t client)
* Holding down LMB will make character walk in the direction of the cursor
* Mouse follow mode - press Ctrl+F for your character to follow the cursor
* Drop/Transfer identical items (taken from romovs client)
* Item QL display on icon (from enders)
* Kin online/offline message (from enders)
* FEP and hunger meters (from k-t client) and attention meter
* Notification when attributes are changed (from k-t client)
* Quickhand slots (based on romovs client)

### Keys
* CTRL + N - Night vision toggle
* CTRL + W - World tooltip toggle
* CTRL + F - Follow mouse
* CTRL + G - Grid overlay
* CTRL + V - View filter (hide trees, plants, houses and fences)
* CTRL + D - Object effect radius toggle
* CTRL + 1 - Take/put item in right hand slot
* CTRL + 2 - Take/put item in left hand slot
* CTRL + Q - Hide/Unhide item from cursor
* CTRL + ALT + Left Click - Drop identical items
* CTRL + Right Click - Transfer identical items between inventory and container
* Inventory transfer
  * SHIFT + Scroll UP - Transfer highest QL item from container to inventory
  * SHIFT + Scroll DN - Transfer highest QL item from inventory to container
  * CTRL + SHIFT + Scroll UP - Transfer lowest QL item from container to inventory
  * CTRL + SHIFT + Scroll DN - Transfer lowest QL item from inventory to container

### Minimap improvements
* Larger
* Improved caching
* Player and party arrows
* Option to enable dragging
* Option to show grid and view radius
* Option to show specific boulders, trees and bushes
* Option to show all players (taken from romovs client)
* Option to show arrows (for those tired loosing them when hunting)
* Hold middle mouse button to drag
* CTRL + Middle button to center on player

*Partially based on work by k-t and romovs*

### Running

* To run the client on Windows double click **run.bat** or **run-debug.bat**.
* On other OS: 
  
  ```java -Xms512m -Xmx1024m -jar hafen.jar -U http://game.havenandhearth.com/hres/ game.havenandhearth.com```

* To run map stitcher on Windows double click combiner.bat 
* On other OS: ```java -jar combiner.jar```

### Links

* [H&H forum thread](http://www.havenandhearth.com/forum/viewtopic.php?f=49&t=40945)
