
	// send a HTTP POST request to the server
	function post(okFunction, failFunction, uri, data) {
	    var xhttp = new XMLHttpRequest();
	    xhttp.onreadystatechange = function() {
	        if (this.readyState > 3 && this.status == 200) {
	        	okFunction(this);
	        }
	        else if (this.readyState > 3 && this.status > 200) {
	            failFunction(this.status);
	        }			        
	    };
	    xhttp.open("POST", uri); 
	    //xhttp.withCredentials = true;
	    xhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	    xhttp.send(data);
	}
	
	function showTodoItem(todoDiv, obj, key) {
		var temp = document.getElementsByTagName("template")[0]; // todo template
		var clone = temp.content.cloneNode(true);
		todoDiv.appendChild(clone);
		var cloneDiv = document.getElementById("0");
		// todo id goes here:
		cloneDiv.id = obj[key].id;
		cloneDiv.style.display = "block";
		cloneDiv.getElementsByClassName('card-title')[0].innerHTML = obj[key].id;
		// todo text content goes here:
		cloneDiv.getElementsByClassName('card-body')[0]
			.getElementsByClassName('card-text')[0]
				.innerHTML =
					obj[key].msg
						.replace(/Deadline:/g, '<strong>Deadline:</strong>')
						.replace(/(?:\r\n|\r|\n)/g, '<br>');
	}	
	
	function showDoneItem(doneDiv, obj, key) {
		var temp = document.getElementsByTagName("template")[1]; // done template
		var clone = temp.content.cloneNode(true);
		doneDiv.appendChild(clone);
		var cloneDiv = document.getElementById("00");
		// done id goes here:
		cloneDiv.id = obj[key].id;
		cloneDiv.style.display = "block";
		cloneDiv.getElementsByClassName('card-title')[0].innerHTML = obj[key].id;
		// done text content goes here:
		cloneDiv.getElementsByClassName('card-body')[0]
			.getElementsByClassName('card-text')[0]
				.innerHTML =
					obj[key].msg
						.replace(/Deadline:/g, '<strong>Deadline:</strong>')
						.replace(/(?:\r\n|\r|\n)/g, '<br>');		
	}
		
	// render the (todo and done) items we got from the server as JSON response
	function getAllTodosCallback(data) {
		var obj;
		try {
			obj = JSON.parse(data.responseText);
		} catch (err) {
			console.log("Unable to parse JSON " + data.responseText);
			return;
		}
		var todoDiv = document.getElementById('todoItems');
		todoDiv.innerHTML = "";
		var doneDiv = document.getElementById('doneItems');
		doneDiv.innerHTML = "";
		for (var key in obj) {
			if (obj.hasOwnProperty(key)) {
				var type = obj[key].type;
				if (type === "todo") {
					showTodoItem(todoDiv, obj, key);
				}
				else { // done item
					showDoneItem(doneDiv, obj, key);
				}
			}
		}	
	}

	function setAsDone(elem) {
		post(getAllTodosCallback, failCallback, "/setAsDone", "text=" + elem.closest('.card').id);
	}
		
	function setAsTodo(elem) {
		post(getAllTodosCallback, failCallback, "/setAsTodo", "text=" + elem.closest('.card').id);
	}			

	function removeTodo(elem) {
		var ret = confirm("Are you sure you want to remove this item?")
		if (ret === true) {
			post(getAllTodosCallback, failCallback, "/removeTodo", "text=" + elem.closest('.card').id);
		}
	}			
		
	function addTodo() {
		var elem = document.getElementById("newTodoText");
		var content = elem.value;
		elem.value = "";
		var deadlineElem = document.getElementById("timepicker1")
		var deadline = deadlineElem.value;
		if (deadline !== "") {
			content += "\n\n" + "Deadline: " + deadline;
			deadlineElem.value = "";
		}
		post(getAllTodosCallback, failCallback, "/addTodo",
				"text=" + encodeURIComponent(content));
	}
	
	function failCallback(code) {
		alert("Failed with code " + code);
	}
		
	// after page load, get all
	(function() {
		post(getAllTodosCallback, failCallback, "/getAllTodos", "");
	})();