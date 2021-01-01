function makemylist() {
	let txt = "myList:"
	var i;
	for (i = 0; i < 10; i++) {
		txt += "<li>new foo " + i.toString() + "</li>"
	}
	document.getElementById("taskList").innerHTML = txt
}
