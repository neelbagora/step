// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/* global variables */
const AIRPLANES = "These are photos I have taken from the flight simulator, X-Plane 11. In my free time, I love to simulate air traffic communication during my flights so I use the VATSIM network. The VATSIM network is a plugin used with X-Plane that allows me to communicate with real people (acting as air traffic controllers) and fly under the supervision of air traffic controllers. The callsigns mentioned below are callsigns used on the VATSIM online air traffic network.";
const PROJECTS = "These are pictures of the projects I have worked on throughout the years. I am mainly experienced in Java, C, C++, and Python. A lot of my projects were built when I wanted to learn something new, for example, the currency converter gave me a chance to learn both Kotlin and API calls in Kotlin/Java. The MIDI music program was a way for me to learn how binary files work in real world context.";
const MISCELLANEOUS = "These are pictures that I thought were cool but could not categorize.";
const SHOWALL = "These are all the pictures that I have posted on this website, pictures can be filtered using the menu bar above.";
let url_data = '/data';
let userId = undefined;


/**
 * filterPicturesBySelection is a function responsible for updating
 * the gallery images. Based on the selection, the gallery will show 
 * images that are relevant to the selection.
 *
 * @param selection specified category that viewer is viewing images.
 */
function filterPicturesBySelection(selection) {
	updateGalleryText(selection);
	var index;
	column = document.getElementsByClassName("column");
	if (selection == "all") {
			selection = "";
	}
	for (index = 0; index < column.length; index++) {
		removeElementClass(column[index], "show");
		if (column[index].className.indexOf(selection) > -1) {
			addElementClass(column[index], "show");
		}
	}
}

/**
 * addElementClass is a function meant to dynamically add classes to the
 * html page. For example, the show class can be added to the images to
 * reflect the characteristics of the specified class.
 *
 * @param element HTML element that the new class will be appended to.
 * @param name    Class name to be added to HTML element.
 */
function addElementClass(element, name) {
	var index, originalClass, newClass;
	originalClass = element.className.split(" ");
	newClass = name.split(" ");
	for (index = 0; index < newClass.length; index++) {
		if (originalClass.indexOf(newClass[index]) == -1) {
			element.className += " " + newClass[index];
		}
	}
}

/**
 * removeElementClass is a function meant to dynamically remove classes to
 * html elements. Refer to addElementClass to better understand the purpose
 * of this function.
 *
 * @param element HTML element that the class name will be removed from.
 * @param name    Class name to be removed from HTML element.
 */
function removeElementClass(element, name) {
	var index, originalClass, newClass;
	originalClass = element.className.split(" ");
	newClass = name.split(" ");
	for (index = 0; index < newClass.length; index++) {
		while (originalClass.indexOf(newClass[index]) > -1) {
			originalClass.splice(originalClass.indexOf(newClass[index]), 1);     
		}
	}
	element.className = originalClass.join(" ");
}

/**
 * updateGalleryText takes in a specified selection and updates
 * the header text of the gallery to reflect the images specifed.
 * For example, the 'airplanes' specification will display the
 * AIRPLANES constant defined above.
 *
 * @param elementName name of category that the gallery is displaying.
 */
function updateGalleryText(elementName) {
	if (elementName === 'all'){
		document.getElementById('gallery-text').innerText = SHOWALL;
	}
	else if (elementName === 'airplanes') {
		document.getElementById('gallery-text').innerText = AIRPLANES;
	}
	else if (elementName === 'projects') {
		document.getElementById('gallery-text').innerText = PROJECTS;
	}
	else {
		document.getElementById('gallery-text').innerText = MISCELLANEOUS;
	}
}

/**
 * createCommentData() is the function responsible for obtaining the comment
 * data from the Java servlet and appends data on the 'comments-container' of
 * the html page. Default url is '/data'.
 *
 * @param firstRun specifies if the drop down element needs to be rebuilt.
 */
function createCommentData(firstRun) {
  var commentForm = document.getElementById('comment-form');
  commentForm.action = '/data';
  if (firstRun) {
    url_data = '/data';
    document.getElementById('limit-selector').innerHTML = '<option value="0">Show All Comments:</option> ';
  }
  fetch(url_data).then(response => response.json()).then((commentData) => {
	  const commentElement = document.getElementById('comments-container');
		limit = commentData.length;
		document.getElementById('comments-container').innerHTML = "";

    for (var i = 0; i < commentData.length; i++) {
      if (firstRun) {
          const selector = document.getElementById('limit-selector');
          const child = document.createElement('option');
          child.innerText = i + 1;
          child.value = i + 1;
          child.className = ''
          selector.appendChild(child);
      }
      const comment = commentData[i];
      console.log(comment);
      commentElement.appendChild(createCommentNode(comment.name, comment.text, comment.date));
      var deleteBtn = configureDeleteButton(comment);
      if (deleteBtn != undefined) {
        commentElement.appendChild(deleteBtn);
        console.log(userId);
        if (comment.user_id === userId) {
          document.getElementById(comment.id + "form").style.display = 'block';
        }
        else {
          document.getElementById(comment.id + "form").style.display = 'none';
        }
      }
      commentElement.appendChild(document.createElement('hr'));
    }
	});

}

/**
 * function used by buttons to submit the form that is filled out on the HTML.
 */
function submitForm() {
  document.getElementById('email').value = userId;
  document.getElementById('comment-form').submit();
}

/**
 * configureDeleteButton takes in the comment retrieved from server
 * and appends new button elements within form elements. Delete Button
 * is used to send post requests to the '/delete-data' url.
 *
 * @param  comment comment element that will have the button.
 * @return         new button created
 */
function configureDeleteButton(comment) {
  console.log(userId);
  var form = document.createElement('form');
  form.method = "POST";
  form.id = comment.id + "form";
  var deleteBtn = document.createElement('input');
  deleteBtn.className = 'btn';
  deleteBtn.type = 'submit';
  deleteBtn.value = 'Delete Comment';
  deleteBtn.id = comment.id;
  form.action = '/delete-data?id=' + comment.id;
  deleteBtn.onclick = "createCommentData(true);";
  form.appendChild(deleteBtn);
  return form;
}

/**
 * function used by the limit-selector element to specify number
 * of elements to post. Configured url_data upon change.
 */
function selectFunction() {
  var selection = document.getElementById("limit-selector").value;
  if (selection == 0) {
      createCommentData(true);
      return;
  }
  url_data = '/data?limit=' + selection;
  createCommentData(false);
}

/** 
 * validateForm ensures that the input in the comment form contains valid
 * values.
 *
 * @return boolean expression verifying that input is correct.
 */
function validateForm() {
	var fname = document.forms["comment-form"]["fname"].value;
	var message = document.forms["comment-form"]["message"].value;
	if (fname == "") {
			alert("First Name is empty!");
			return false;
	}

	if (fname.trim() === '') {
    alert("First Name is invalid!");
    return false;
	}

	if (message == "") {
    alert("Message is empty!");
    return false;
	}

	if (message.trim() === '') {
    alert("Message is invalid!");
    return false;
	}
}

/**
 * createCommentNode takes in the comment data and returns the div element to be
 * appended to the parent div element of the HTML page.
 *
 * @param name Name of commenter
 * @param text Message on the comment.
 * @param date Date of the comment.
 * @return     CommentNode that is created by the supplying information.
 */
function createCommentNode(name, text, date) {
	const commentNode = document.createElement('div');
	commentNode.className = "comment";
	const nameNode = document.createElement('h4');
	nameNode.innerText = name;
	const dateNode = document.createElement('h5');
	dateNode.innerText = date;
	const textNode = document.createElement('h5');
	textNode.innerText = text;        
	commentNode.appendChild(nameNode);
	commentNode.appendChild(dateNode);
	commentNode.appendChild(textNode);
	
	return commentNode;
}

// Adds click listener and listener actions to each button.
var btnContainer = document.getElementById("myBtnContainer");
var btns = document.getElementsByClassName("btn");
for (var index = 0; index < btns.length; index++) {
	btns[index].addEventListener("click", function() {
    var current = document.getElementsByClassName("active");
    current[0].className = current[0].className.replace(" active", "");
    this.className += " active";
	});
}


/**
 * verifyLoginCredentials is called everytime the body of the DOM loads, it retrieves the JSON
 * ID from /login and sets the global variable userId to that value, thus indicating that the
 * page is being viewed by a logged in member. Upon obtaining JSON data, the comment form is
 * available for the user to use.
 */
function verifyLoginCredentials() {
  fetch('/login').then(response => response.json()).then((login_status) => {
    userId = login_status;
    document.getElementById('lock-image').className = 'fa fa-unlock';
    document.getElementById('comment-form').style.display = 'block';
    document.getElementById('sign-in').style.display = 'none';
    document.getElementById('change-user').style.display = 'block';
    createCommentData(true);
    return;
  });
}

function editNickName() {
  document.getElementById('uname-label').style.display = 'block';
  document.getElementById('uname').style.display = 'block';
}
