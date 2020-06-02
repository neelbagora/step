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


filterPicturesBySelection("all");

/*
 * filterPicturesBySelection is a function responsible for updating
 * the gallery images. Based on the selection, the gallery will show 
 * images that are relevant to the selection.
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
        if (column[index].className.indexOf(selection) > -1) addElementClass(column[index], "show");
    }
} /* filterPicturesBySelection() */

/*
 * addElementClass is a function meant to dynamically add classes to the
 * html page. For example, the show class can be added to the images to
 * reflect the characteristics of the specified class.
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
} /* addElementClass() */

/*
 * removeElementClass is a function meant to dynamically remove classes to
 * html elements. Refer to addElementClass to better understand the purpose
 * of this function.
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
} /* removeElementClass() */

/*
 * updateGalleryText takes in a specified selection and updates
 * the header text of the gallery to reflect the images specifed.
 * For example, the 'airplanes' specification will display the
 * AIRPLANES constant defined on line 17.
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
} /* updateGalleryText() */

/*
 * createCommentData() is the function responsible for obtaining the comment
 * data from the Java servlet and appends data on the 'comments-container' of
 * the html page.
 */
function createCommentData() {
    fetch('/data').then(response => response.json()).then((commentData) => {
        console.log('begin task');
        const COMMANDSELEMENT = document.getElementById('comments-container');
        for (var i = 0; i < commentData.length; i++) {
            const COMMENT = commentData[i];
            console.log(COMMENT);
            COMMANDSELEMENT.appendChild(createCommentNode(COMMENT.name, COMMENT.text, COMMENT.date));
            COMMANDSELEMENT.appendChild(document.createElement('hr'));
        }
    });
} /* createCommentData() */

/*
 * createCommentNode takes in the comment data and returns the li element to be
 * appended to the ul element of the HTML page.
 */
function createCommentNode(name, text, date) {
    const COMMENTNODE = document.createElement('li');
    COMMENTNODE.innerText = name + "\n" + date + "\n" + text;
    
    return COMMENTNODE;
} /* createCommentNode() */

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