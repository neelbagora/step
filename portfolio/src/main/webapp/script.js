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
filterPicturesBySelection("all")
function filterPicturesBySelection(selection) {
    updateGalleryText(selection);
    var picture, index;
    column = document.getElementsByClassName("column");
    if (selection == "all") {
        selection = "";
    }
    for (index = 0; index < column.length; index++) {
        removeClass(column[index], "show");
        if (column[index].className.indexOf(selection) > -1) addClass(column[index], "show");
    }
}

function addClass(element, name) {
    var index, originalClass, newClass;
    originalClass = element.className.split(" ");
    newClass = name.split(" ");
    for (index = 0; index < newClass.length; index++) {
        if (originalClass.indexOf(newClass[index]) == -1) {
            element.className += " " + newClass[index];
        }
    }
}

function removeClass(element, name) {
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

function updateGalleryText(elementName) {
    const airplane = "These are photos I have taken from the flight simulator, X-Plane 11. In my free time, I love to simulate air traffic communication during my flights so I use the VATSIM network. The VATSIM network is a plugin used with X-Plane that allows me to communicate with real people (acting as air traffic controllers) and fly under the supervision of air traffic controllers. The callsigns mentioned below are callsigns used on the VATSIM online air traffic network.";
    const projects = "These are pictures of the projects I have worked on throughout the years. I am mainly experienced in Java, C, C++, and Python. A lot of my projects were built when I wanted to learn something new, for example, the currency converter gave me a chance to learn both Kotlin and API calls in Kotlin/Java. The MIDI music program was a way for me to learn how binary files work in real world context.";
    const miscellaneous = "These are pictures that I thought were cool but could not categorize.";
    const showAll = "These are all the pictures that I have posted on this website, pictures can be filtered using the menu bar above.";
    if (elementName === 'all'){
        document.getElementById('gallery-text').innerText = showAll;
    }
    else if (elementName === 'airplanes') {
        document.getElementById('gallery-text').innerText = airplane;
    }
    else if (elementName === 'projects') {
        document.getElementById('gallery-text').innerText = projects;
    }
    else {
        document.getElementById('gallery-text').innerText = miscellaneous;
    }
}

// Add active class to the current button (highlight it)
var btnContainer = document.getElementById("myBtnContainer");
var btns = document.getElementsByClassName("btn");
for (var index = 0; index < btns.length; index++) {
    btns[index].addEventListener("click", function() {
    var current = document.getElementsByClassName("active");
    current[0].className = current[0].className.replace(" active", "");
    this.className += " active";
  });
}