$(document).ready(function() {

    $("#login").click(function() {
        var userName = $("#username").val();
        var passWord = $("#password").val();
        $("#showUsername").text(userName);
        $("#showUsernameMD5").text(md5(userName));
        $("#showPassword").text(passWord);
        $("#showPasswordMD5").text(md5(passWord));

        if ("3be043014dec4bca29c01d8802ed292d" == md5(userName) &&
            "ca526eec71b66549f2b01a23ec0b8ece" == md5(passWord)) {
            getDecryptResume(userName, passWord);
        } else if ("8f43d75cc76f8d6ea49b67e618e678a0" == md5(userName) &&
            "158ec274fc60981bde7ad54cf7d44f76" == md5(passWord)) {
            getDecryptResume(userName, passWord);
        }
    });
});

function getDecryptResume(username, password) {
    var folderName = md5(username + password).substr(0, 8);
    $.getJSON("resource/" + folderName + "/json/resume.json", function(data) {
        var $jsontip = $("#content");
        var resumeContent;
        $jsontip.empty();
        $.each(data, function(infoIndex, info) {
            resumeContent = info["content"];
        })
        resumeContent = resumeContent.replace(/@/g, password.substr(1, 1));
        resumeContent = resumeContent.replace(/\$/g, password.substr(3, 1));
        resumeContent = resumeContent.replace(/#/g, password.substr(5, 1));
        var decryptResume = unescape(resumeContent);
        $("#main").fadeOut();
        $jsontip.html(decryptResume);
    })
}
