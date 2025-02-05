// <!--<comment>
//     This version of registration page use built-in HTML form attributes to validate the input fields.
//     This feature is recently supported by most of the modern browsers. You can find more details on the following link:
//     https://developer.mozilla.org/en-US/docs/Learn/Forms/Form_validation
// </comment>-->



const checkPwMatch = (pw1,pw2) => {
    return pw1.value === pw2.value;
}

const createButton = () => {
    var container = document.querySelector("#register_submit_container")
    var reg = document.querySelector("#register_submit");
    //check if already present
    if (reg) {
        return;
    }

    var button = document.createElement("input");
    button.id = "register_submit";
    button.type = "submit";
    button.value = "Registrati";
    button.classList.add("btn", "btn-primary");
    container.appendChild(button);
}

const removeButton = () => {
    var container = document.querySelector("#register_submit_container")
    var reg = document.querySelector("#register_submit");
    if(reg){
        container.removeChild(reg);
    }
}

const checkFormValidity = () => {
    const register_form = document.getElementById('register_form');
    const password = document.getElementById('password');
    const password_confirm = document.getElementById('password_confirm');

    const isPwMatch = checkPwMatch(password, password_confirm);
    console.log(isPwMatch);
    console.log(register_form.checkValidity());
    if(register_form.checkValidity() && isPwMatch){
        console.log('checkValidity true');
        createButton();
    }else {
        console.log('checkValidity false');
        removeButton();
    }
}

document.addEventListener('DOMContentLoaded', function(){
    const register_form = document.getElementById('register_form')
    removeButton();
    register_form.addEventListener('input', checkFormValidity);
});


