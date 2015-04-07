/**
 * Created by Vlad on 04.04.2015.
 */

function functionAnimatedShowHide(mID) {
    var hidden_el = document.getElementById(mID),
        remove_class = "slide-down",
        add_class = "slide-up",
        is_showing= hidden_el.classList.contains(remove_class);
    /*element_classes = (" " + hidden_el.className + " ").replace(/[\n\t\r]/g, " "),
     is_showing = element_classes.indexOf(" " + remove_class + " ") > -1;*/

    if (!is_showing) {
        // Switch variable values
       // remove_class = [add_class, add_class = remove_class][0];
        hidden_el.classList.add(remove_class);
        hidden_el.classList.remove(add_class);
        hidden_el.parentNode.childNodes[1].classList.remove("closed");
    }
    else {
        hidden_el.classList.add(add_class);
        hidden_el.classList.remove(remove_class);
        hidden_el.parentNode.childNodes[1].classList.add("closed");
    }

    // Remove the previous class (if present) and add the new class
    //hidden_el.className = (element_classes.replace(" " + remove_class + " ", "") + " " + add_class + " ").trim();

}


function functionShowHide(mId) {
    var obj = document.getElementById(mId);
    toggle(obj);

}

function getRealDisplay(elem) {
    if (elem.currentStyle) {
        return elem.currentStyle.display;
    } else if (window.getComputedStyle) {
        var computedStyle = window.getComputedStyle(elem, null);

        return computedStyle.getPropertyValue('display');
    }
}

function hide(el) {
    if (!el.hasAttribute('displayOld')) {
        el.setAttribute("displayOld", el.style.display);
    }

    el.style.display = "none";
}

displayCache = {};

function isHidden(el) {
    var width = el.offsetWidth, height = el.offsetHeight,
        tr = el.nodeName.toLowerCase() === "tr";

    return width === 0 && height === 0 && !tr ?
        true : width > 0 && height > 0 && !tr ? false : getRealDisplay(el);
}

function toggle(el) {
    isHidden(el) ? show(el) : hide(el);
}

function show(el) {

    if (getRealDisplay(el) != 'none') return;

    var old = el.getAttribute("displayOld");
    el.style.display = old || "";

    if (getRealDisplay(el) === "none") {
        var nodeName = el.nodeName, body = document.body, display;

        if (displayCache[nodeName]) {
            display = displayCache[nodeName];
        } else {
            var testElem = document.createElement(nodeName);
            body.appendChild(testElem);
            display = getRealDisplay(testElem);

            if (display === "none") {
                display = "block";
            }

            body.removeChild(testElem);
            displayCache[nodeName] = display;
        }

        el.setAttribute('displayOld', display);
        el.style.display = display;
    }
}





