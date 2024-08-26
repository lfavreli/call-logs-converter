// Register plugins
$.fn.filepond.registerPlugin(FilePondPluginFileValidateType, FilePondPluginFileValidateSize);

// Initialize FilePond instance
const pond = $('#dropzone').filepond({
    name: "filepond",
    allowMultiple: false,
    allowReplace: false,
    maxFiles: 1,
    maxFileSize: '20MB',
    acceptedFileTypes: ['application/pdf'],
    instantUpload: false,
    labelIdle: `
        <div class="filepond--label-idle">
            <p class="filepond--label-idle-txt-1">Drag & Drop your PDF here or <span class="filepond--label-action">Click</span> to upload</p>
            <div class="filepond--label-idle-btn">Choose File</div>
            <p class="filepond--label-idle-txt-2">Supported file format: PDF<br>Max file size: 5MB</p>
        </div>
    `,
    credits: false
});

// Remove Skeleton when FilePond is initialized and show hero section
$('#dropzone').on('FilePond:init', function (e) {
    $('.skeleton').remove();
    $('.hero').css("display", "flex");
});

// Show convert button when file is added
$('#dropzone').on('FilePond:addfile', function (e) {
    $(".hero-btn-convert").css("display", "block");
    $('.hero').css("height", "150px");
});

// Hide convert button when file is removed
$('#dropzone').on('FilePond:removefile', function (e) {
    $(".hero-btn-convert").css("display", "none");
    $('.hero').css("height", "200px");
});

$(".hero-btn-convert").click(function () {

    const document = $('#dropzone').filepond('getFile', 0);
    if (document && document.id && document.file) {
        let formData = new FormData();
        formData.append('file', document.file);

        fetch('/api/call-logs', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.text())
            .then(callLogId => {
                const callLogName = getFilenameWithoutExtension(document.file.name);
                const csvFileName = callLogName + ".csv";
                checkStatus(callLogId, csvFileName);
                $('.file-info-label').text(csvFileName);
                showResult();
            }).catch(error => {
                console.error(error);
                alert("Failed to convert file: replace it or contact us 👋");
            });

    } else {
        alert("Please select a file");
    }
});

function checkStatus(callLogId, csvFileName) {
    const eventSource = new EventSource(`/api/call-logs/${callLogId}/status`);
    showLoader();
    eventSource.onmessage = function (event) {
        const data = JSON.parse(event.data);
        const { callLogStatus } = data;
        if (callLogStatus === 'COMPLETED') {
            console.log("Conversion completed!", '<---- "Conversion completed!"');;
            eventSource.close();
            showRemove();
            showDone();
            $('.download-link').removeClass('disabled');

            $('.download-link')
                    .attr('href', `/api/call-logs/${callLogId}/download`)
                    .attr('download', csvFileName);
        }
    };

    eventSource.onerror = function (e) {
        console.error('Error with EventSource connection.');
        eventSource.close();
        $('.loader-container').css("display", "none");
    };
}

function getFilenameWithoutExtension(filename) {
    const lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex === -1) return filename; // No extension found
    return filename.substring(0, lastDotIndex);
}


function showLoader() {
    $('.loader-icon').removeClass('hide').addClass('show');
    $('.remove-icon').removeClass('show').addClass('hide');
}

function showDone() {
    $('.done-icon').removeClass('hide').addClass('show');
}

function hideDone() {
    $('.done-icon').removeClass('show').addClass('hide');
}

function showRemove() {
    $('.loader-icon').removeClass('show').addClass('hide');
    $('.remove-icon').removeClass('hide').addClass('show');
}

function showHero() {
    $('.hero').css("display", "flex");
    $('.result').css("display", "none");
}

function showResult() {
    $('.hero').css("display", "none");
    $('.result').css("display", "flex");
}

// Call removeFiles on FilePond when clicked on .remove-icon-area
$('.remove-icon-area').click(function () {
    $('#dropzone').filepond('removeFiles');
    hideDone();
    resetDownloadButton();
    showHero();
});

function resetDownloadButton() {
    $('.download-btn').addClass('disabled');
    $('.download-link').removeAttr('href');
    $('.download-link').removeAttr('download');
}
