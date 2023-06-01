Due to time constraints following couldn't be achieved: 
* Security of the application, currently anonymous user but only with user with email can modify 
* Restricting inputs to certain characters to avoid security exploit but need to know which are all to be included. 
* Covering all the possible testing scenarios, it would take really time and approach to cover all the scenarios, fow now covered most basic paths.. 
* Unit testing - approach followed for now is TDD using spring boot testing
  * time permitted would have smaller test cases at unit level. 
* Docker, kubernetes configuration - again couldn't do it with time constraint
* Metrics, performance testing considering out of scope for this exercise
* super clean code - couldn't do it, I know I could do improve cases like having more string constants but I tried my level best to follow clean code principles
* definitely missed java docs - I will see if I can push another one by adding the java docs
* `dto` models can be it's own project and should have been jar to publish and get it from repo. But here made it simple case. 
* consistency with lombok equal to custom equal - created where I feel it is mandatory to show case the need of custom equals, obvious one left it to lombok. 