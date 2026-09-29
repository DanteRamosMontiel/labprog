const hamButton = document.querySelector(".menu-ham")
const sideMenu = document.querySelector(".side-menu")

let firstLoad = true

hamButton.addEventListener("click", () => {
    if (firstLoad){
        sideMenu.classList.toggle("side-menu-spawn")
        firstLoad = false
        return
    }else{
        sideMenu.classList.toggle("side-menu-spawn")
        sideMenu.classList.toggle("side-menu-despawn")
    }
})