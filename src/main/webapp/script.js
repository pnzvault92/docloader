function loadDocs() {
    $.ajax({
        type: "GET",
        url: "/docs",
        success: function (data) {
            $("tbody").text("");
            data.forEach(function(item){
                var tr = $("<tr></tr>");
                var a = $("<a></a>")
                    .text(item)
                    .attr({
                        href: "/download?name="+item,
                        download: "",
                        title: "Скачать договор"
                    });
                var td1 = $("<td></td>").append(a);

                var aAttribtes = {
                    href: "/download?name="+item,
                    download: "",
                    title: "Скачать договор"
                };
                var button = $("<button></button>")
                    .text("Загрузить документ")
                    .attr({
                        id:item.split(" от ")[0],
                        type: "button",
                        class: "btn"
                    })
                    .click(upload);
                var td2 = $("<td></td>").append(button);
                tr.append(td1,td2);

                $("tbody").append(tr);
            });
        },
        error: function () {
            alert("Ошибка загрузки данных");
        }
    });
}

function upload(event) {
    var id = event.target.id;
    $("input[type=file]").attr("id",id).click();
}

$(document).ready(function() {
    loadDocs();
    $("input[type=file]").hide().change(function () {
        if(this.files.length!==1)
            return;
        var fd = new FormData();
        fd.set("file", this.files[0]);
        fd.set("number", this.id);

        $.ajax({
            type: "POST",
            url: "/upload",
            data: fd,
            processData: false,
            contentType: false,
            success: function (data) {
                if(data!=null){
                    alert("Документ загружен");
                    if(data=="new"){
                        loadDocs();
                    }
                }
                else {
                    alert("Ошибка загрузки документа");
                }
            },
            error: function () {
                alert("Ошибка загрузки документа");
            }
        });
        this.value = "";
    });
    $("button").click(upload);
});