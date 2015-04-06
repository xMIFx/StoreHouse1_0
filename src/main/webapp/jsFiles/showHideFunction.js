/**
 * Created by Vlad on 04.04.2015.
 */
function functionAnimatedShowHide(mId) {
    var obj = document.getElementById(mId);
    var handler = function () {
        hide(obj);
        obj.removeEventListener('webkitTransitionEnd', handler, false);
    };

    if (isHidden(obj)) {
        show(obj);
        obj.classList.remove("hide");
    }
    else {
        obj.addEventListener('webkitTransitionEnd', handler, false);
        obj.classList.add("hide");

    }


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
    el.parentNode.childNodes.item(1).classList.add("closed");
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
    el.parentNode.childNodes.item(1).classList.remove("closed");
}





