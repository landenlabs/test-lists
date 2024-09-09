
# LanDen Labs - List (ArrayList and LinkedList) Bench Test
DT: 8-Sept-2024
BY: Dennis Lang
  
Measure list operations per millisecond to compare how **ArrayList** and **LinkedList**
perform specific container operations.

Operations tested
* Insert item to front of list
* Append item to end of list
* Get accessor
* Contains method
* Delete first item
* Delete middle item
* Delete last item
* Sort list

This unit test is based off an article and code by Renan Schmitt.

* Article - 
https://medium.com/java-performance/performance-arraylist-vs-linked-list-966bde4cfe75
* Github unit tester -
https://github.com/renan-schmitt/list-compare-performance


### Notes on application

* Program is written in **Java**.
* Build and run using Intellij


### Console output (run on Mac M3) 

```  
[Start] Mac OS X aarch64
                     1,000         10,000        100,000  ; # Elements
TestAddFIRST      
  ArrayList         25,179          2,724            246  ; Ops/Milli
  LinkedList       227,880        231,088        211,488  ; Ops/Milli
TestAddLAST       
  ArrayList        518,762        492,387        577,722  ; Ops/Milli
  LinkedList       327,233        378,741        342,057  ; Ops/Milli
TestGet           
  ArrayList        211,807        218,321        305,594  ; Ops/Milli
  LinkedList         3,272            270             26  ; Ops/Milli
TestContains     
  ArrayList          4,948            467             51  ; Ops/Milli
  LinkedList         1,227            122             12  ; Ops/Milli
TestDelFIRST      
  ArrayList         26,436          2,607            233  ; Ops/Milli
  LinkedList       464,821        434,905        420,579  ; Ops/Milli
TestDelMIDDLE    
  ArrayList         34,921          3,672            330  ; Ops/Milli
  LinkedList         6,381            540             53  ; Ops/Milli
TestDelLAST       
  ArrayList        377,187        369,720        360,139  ; Ops/Milli
  LinkedList       308,185        310,209        300,699  ; Ops/Milli
[Done]
```  

### Summary 
Bench testing is not always an accurate representation of real world usage. 
Compiler optimization can alert results and bench tests can often be biased 
by supporting logic and memory patterns. 

This very limited test reveals a few interesting facts. 
1. Adding to the front of an ArrayList is expensive because the list data has to be shifted in memory while link list just adds a new node. 
2. ArrayList can be optimized to avoid reallocation of memory by setting its capacity prior to adding data.  This aspect is not part of this bench test. 
3. LinkedLists have a fixed cost to add a new node or remove a node. This is not clearly revealed in the bench test because of overhead in the tests. 
4. LinkedList random access is fast near the front or back with the middle being the most expensive requiring the longest traversal of the nodes.


### License  
  
```  
Copyright 2020 Dennis Lang  
  
Licensed under the Apache License, Version 2.0 (the "License");  
you may not use this file except in compliance with the License.  
You may obtain a copy of the License at  
  
 https://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software  
distributed under the License is distributed on an "AS IS" BASIS,  
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
See the License for the specific language governing permissions and  
limitations under the License.  
```



