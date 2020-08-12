TODO web app

Saves TODO items as ASCII files on disk.


The basic message flow is as follows (open the file in a wider view to view properly):

                   |------------------------SERVER----------------------------------------|

USER                     SERVER MAIN                     CONTROLLER           TODO FILES                        
(BROWSER)                (JETTY)                       
    \
     \ HTTP
      \ GET/POST/
       \ AJAX
        +------------> MainRequestRouter.java
                       (route to controller) ---------> TodoController <------> 1.todo
                      /                    <------------+                       2.todo
                     /                        JSONObject
                    /
                   / HTTP
<-----------------+  (HTML/JSON)
