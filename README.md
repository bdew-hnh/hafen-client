### Main features:

* Automated mapping, including sticher that can merge maps across sessions
* Option to hide flavour objects - reduces CPU utilization. Can be toggled under Options -> Display Settings. (taken from romov client)
* Daylight mode. Ctrl+N to toggle. (taken from k-t client)

### Running

* To run the client on Windows double click **run.bat** or **run-debug.bat**.
* On other OS: 
  
  ```java -Xms512m -Xmx1024m -jar hafen.jar -U http://game.havenandhearth.com/hres/ game.havenandhearth.com```

* To run map stitcher on Windows double click combiner.bat 
* On other OS: ```java -jar combiner.jar```

### Links

* [H&H forum thread](http://www.havenandhearth.com/forum/viewtopic.php?f=49&t=40945)
