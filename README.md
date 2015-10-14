### Main features:

* Automated mapping, including stitcher that can merge maps across sessions
* Automatic curiosity study (see below)
* Improved fonts and sizes
* Improved minimap (see below)
* Log off on client close - closing the window will send a logoff command and exit after successful logoff
* Completion percent text and estimated timer on items (e.g. in study window)
* Transfer by highest/lowest QL with mousewheel (optional)
* Nightvision mode (with configurable brightness)
* Hotkey to hide item from cursor (see below)
* Account management (based on romovs)
* Custom resource folder support - put a folder named "res" in client location to load custom resources
* Quick hand equipment slots and hotkeys
* Option to prevent accidental item drop - never drown your keys again
* Option to mark studied curiosities - green X on curiosities you already study, and red X on those that you can't due to lacing attention
* Option to display item quality on icon with many different modes
* Option to show player/animal movement vectors
* Option to always show the long tooltip on items
* Option to show plant and tree growth stages
* Option to show damage on objects
* Option to selectively hide trees, bushes, plants, fences and houses
* Option to hide flavour objects - reduces CPU utilization (taken from romovs client)
* Option to disable dynamic lights
* Option to show object (mine supports, etc.) radius (based on romovs client)
* Option to limit FPS in background and foreground mode (replaces hz/bghz commands)
* Option to show kin online/offline message (from enders)
* Option to show server time (from APXEOLOG)
* Orthogonal camera lock mode option (normal, 0/90/180/270, 8 way, free)
* Daylight mode - Ctrl+N to toggle (taken from k-t client)
* Grid overlay (somewhat based on k-t client)
* Holding down LMB will make character walk in the direction of the cursor
* Mouse follow mode - press Ctrl+F for your character to follow the cursor
* Drop/Transfer identical items (taken from romovs client)
* FEP and hunger meters (from k-t client) and attention meter
* Notification when attributes are changed (from k-t client)
* Quickhand slots and hotkeys (based on romovs client)
* Chat timestamps

###Warning - Account Management
If you check "save login" on login screen - the client will save your password, in clear text, either in your registry (Windows) or user folder (Mac/Linux).
This is inherently unsafe. It was added as a trade off between convenience and security after requests from multiple users and friends.
I do not recommend using this option.

### Automatic study
* Enable by ticking AUTO in study report
* Will automatically insert curiosities to study from player inventory
* Checks for required attention and that the same curiosity is not being studied
* All other inventory windows will be closed before moving a curiosity
* Known issue: Does not check if curiosity can't fit due to size.
 * If this happens it will go into a loop trying to insert it.
 * Will be fixed in the future

### Cursor item hide
* Hides the item currently "held" by the mouse cursor
* Press again to restore the item
* You can walk around, interact with objects and inventories without putting the item down
* You won't be able to pick up other items
* After relogging the item will reappear on the cursor

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
* CTRL + X - Log off
* CTRL + ALT + Left Click - Drop identical items
* CTRL + Right Click - Transfer identical items between inventory and container
* CTRL + SHIFT + Right Click with item on world object - mass add items (for filling throughs, tar kilns, etc.)
* Scroll sorting (if enabled)
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
