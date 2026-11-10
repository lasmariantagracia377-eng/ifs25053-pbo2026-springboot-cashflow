# Mengganti username "abdullah_ubaid" menjadi username Anda di seluruh proyek.
# Pakai dari folder proyek:  .\rename-username.ps1 -NewUser budi_santoso
param([Parameter(Mandatory = $true)][string]$NewUser)

$old = "abdullah_ubaid"
$root = Get-Location

# 1) Ganti isi file (package, import, pom.xml, application.yaml)
Get-ChildItem -Recurse -File -Include *.java,*.xml,*.yaml,*.md |
    Where-Object { $_.FullName -notmatch "\\target\\" } |
    ForEach-Object {
        $text = Get-Content $_.FullName -Raw -Encoding UTF8
        if ($text -match $old) {
            Set-Content $_.FullName ($text -replace $old, $NewUser) -Encoding UTF8 -NoNewline
        }
    }

# 2) Ganti nama folder package
Get-ChildItem -Recurse -Directory -Filter "${old}pbo2026springbootcashflow" |
    Sort-Object FullName -Descending |
    ForEach-Object { Rename-Item $_.FullName "${NewUser}pbo2026springbootcashflow" }

Write-Host "Selesai. Ganti juga nama folder proyek menjadi: $NewUser-pbo2026-springboot-cashflow"
