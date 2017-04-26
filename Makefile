TimeTable.jar: TimeTable.class TimeGUI.class src/template.jpg MANIFEST.mf
	jar cfm TimeTable.jar MANIFEST.mf *.class src/template.jpg
	rm *.class

TimeTable.class: src/TimeTable.java
	javac src/TimeTable.java -d .

TimeGUI.class: TimeTable.class src/TimeGUI.java
	javac src/TimeGUI.java -d .
