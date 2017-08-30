TimeTable.jar: TimeTable.class TimeGUI.class src/template.jpg MANIFEST.mf
	cp src/template.jpg template.jpg
	jar cfm TimeTable.jar MANIFEST.mf *.class template.jpg
	rm *.class
	rm template.jpg

TimeTable.class: src/TimeTable.java
	javac src/TimeTable.java -d .

TimeGUI.class: TimeTable.class src/TimeGUI.java
	javac src/TimeGUI.java -d .
