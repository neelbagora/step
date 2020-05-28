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
filterSelection("all")
function filterSelection(c) {
    updateGalleryText(c);
    var x, i;
    x = document.getElementsByClassName("column");
    if (c == "all") c = "";
    for (i = 0; i < x.length; i++) {
        removeClass(x[i], "show");
        if (x[i].className.indexOf(c) > -1) addClass(x[i], "show");
    }
}

function addClass(element, name) {
    var i, arr1, arr2;
    arr1 = element.className.split(" ");
    arr2 = name.split(" ");
    for (i = 0; i < arr2.length; i++) {
        if (arr1.indexOf(arr2[i]) == -1) {element.className += " " + arr2[i];}
    }
}

function removeClass(element, name) {
    var i, arr1, arr2;
    arr1 = element.className.split(" ");
    arr2 = name.split(" ");
    for (i = 0; i < arr2.length; i++) {
        while (arr1.indexOf(arr2[i]) > -1) {
        arr1.splice(arr1.indexOf(arr2[i]), 1);     
        }
    }
    element.className = arr1.join(" ");
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
for (var i = 0; i < btns.length; i++) {
  btns[i].addEventListener("click", function() {
    var current = document.getElementsByClassName("active");
    current[0].className = current[0].className.replace(" active", "");
    this.className += " active";
  });
}