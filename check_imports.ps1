$files = Get-ChildItem -Path .\ -Recurse -Filter "*.kt"
foreach ($file in $files) {
    if ($file.FullName -match "\\build\\") { continue }
    $content = Get-Content $file.FullName
    $imports = $content | Where-Object { $_ -match "^import\s+([a-zA-Z0-9_\.]+)" }
    foreach ($import in $imports) {
        if ($import -match "import\s+[\w\.]+\.([\w]+)(?:\s+as\s+[\w]+)?$") {
            $identifier = $matches[1]
            $nonImportLines = $content | Where-Object { $_ -notmatch "^import\s+" -and $_ -notmatch "^package\s+" }
            $text = $nonImportLines -join "
"
            if ($text -notmatch "\b$identifier\b") {
                Write-Host "$($file.FullName): $import"
            }
        }
    }
}
