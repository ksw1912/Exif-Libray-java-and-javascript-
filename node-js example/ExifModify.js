const exiftool = require('node-exiftool');
const fs = require('fs');
const ep = new exiftool.ExiftoolProcess();

const filePath = "C:/Users/user/Desktop/1.jpg";

// ex: 태그명: "String value"
const data = {
    MyCustomTag: 'test dsfdsf',
};


// 변경된 값 확인
console.log(data.MyCustomTag);

if (!fs.existsSync(filePath)) {
    console.error(`Error: File not found - ${filePath}`);
    ep.close();
} else {
    ep.open() 
        .then(() => ep.writeMetadata(filePath, data, ['overwrite_original']))
        .then(() => ep.readMetadata(filePath, ['-File:all']))
        .then((metadata) => {
            console.log('메타데이터:', metadata.data);
        })
        .catch((err) => {
            console.error('메타데이터 읽기 중 오류 발생:', err);
        })
        .finally(() => {
            ep.close();
        });
}