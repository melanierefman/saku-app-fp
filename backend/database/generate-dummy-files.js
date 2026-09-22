const fs = require('fs');
const path = require('path');

const sampleJpgBuffer = Buffer.from(
  '/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP//////////////////////////////////////////////////////////////////////////////////////wgALCAABAAEBAREA/8QAFBABAAAAAAAAAAAAAAAAAAAAAP/aAAgBAQABPxA=',
  'base64'
);

const minimalPdf = `%PDF-1.4
1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj
2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj
3 0 obj<</Type/Page/MediaBox[0 0 595 842]/Parent 2 0 R/Resources<</Font<</F1 4 0 R>>>>/Contents 5 0 R>>endobj
4 0 obj<</Type/Font/Subtype/Type1/BaseFont/Helvetica>>endobj
5 0 obj<</Length 44>>stream
BT /F1 24 Tf 100 700 Td (Dokumen Dummy SAKU BCA) Tj ET
endstream
endobj
xref
0 6
0000000000 65535 f 
0000000009 00000 n 
0000000052 00000 n 
0000000101 00000 n 
0000000204 00000 n 
0000000262 00000 n 
trailer<</Size 6/Root 1 0 R>>
startxref
357
%%EOF`;

const seederSqlPath = path.resolve(__dirname, 'seeder.sql');
if (!fs.existsSync(seederSqlPath)) {
  console.error('seeder.sql not found at:', seederSqlPath);
  process.exit(1);
}

const content = fs.readFileSync(seederSqlPath, 'utf8');
const regex = /VALUES\s*\([^)]*?'([^']+?\.(?:jpg|jpeg|png|pdf))'/gi;
let match;
let count = 0;

const baseUploadDir = path.resolve(__dirname, '../uploads');

while ((match = regex.exec(content)) !== null) {
  const relPath = match[1];
  const fullPath = path.join(baseUploadDir, relPath);
  const dirName = path.dirname(fullPath);

  if (!fs.existsSync(dirName)) {
    fs.mkdirSync(dirName, { recursive: true });
  }

  if (!fs.existsSync(fullPath)) {
    if (relPath.endsWith('.pdf')) {
      fs.writeFileSync(fullPath, minimalPdf, 'utf8');
    } else {
      fs.writeFileSync(fullPath, sampleJpgBuffer);
    }
    count++;
  }
}

console.log(`Successfully prepared ${count} dummy upload files in ${baseUploadDir}`);
