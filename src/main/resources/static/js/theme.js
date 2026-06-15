(function () {
    function applyTheme(theme) {
        document.documentElement.classList.toggle("dark-mode", theme === "dark");
    }

    var savedTheme = localStorage.getItem("theme") || "light";
    applyTheme(savedTheme);

    document.addEventListener("DOMContentLoaded", function () {
        var button = document.createElement("button");
        button.type = "button";
        button.className = "theme-toggle";

        function refreshLabel() {
            button.textContent = document.documentElement.classList.contains("dark-mode")
                    ? "Mode clair"
                    : "Mode sombre";
        }

        button.addEventListener("click", function () {
            var nextTheme = document.documentElement.classList.contains("dark-mode") ? "light" : "dark";
            localStorage.setItem("theme", nextTheme);
            applyTheme(nextTheme);
            refreshLabel();
        });

        refreshLabel();
        document.body.appendChild(button);
    });
})();
