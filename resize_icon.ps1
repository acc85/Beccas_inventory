Add-Type -AssemblyName System.Drawing

$sourcePath = "C:\Users\Raymo\.gemini\antigravity\brain\4046661e-6475-44ac-8cd2-a42ada75d9dd\manifest_app_icon_1772824544380.png"
$resDir = "C:\Users\Raymo\Documents\AIWork\ManiFest\app\src\main\res"

$sizes = @{
    "mdpi" = @(48, 108)
    "hdpi" = @(72, 162)
    "xhdpi" = @(96, 216)
    "xxhdpi" = @(144, 324)
    "xxxhdpi" = @(192, 432)
}

$bmp = [System.Drawing.Image]::FromFile($sourcePath)

foreach ($dpi in $sizes.Keys) {
    $legacySize = $sizes[$dpi][0]
    $fgSize = $sizes[$dpi][1]
    
    $folder = Join-Path $resDir "mipmap-$dpi"
    if (-not (Test-Path $folder)) {
        New-Item -ItemType Directory -Path $folder | Out-Null
    }
    
    # Generate legacy ic_launcher.png
    $legacyBmp = New-Object System.Drawing.Bitmap $legacySize, $legacySize
    $g = [System.Drawing.Graphics]::FromImage($legacyBmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($bmp, 0, 0, $legacySize, $legacySize)
    $g.Dispose()
    $legacyPath = Join-Path $folder "ic_launcher.png"
    $legacyBmp.Save($legacyPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $legacyBmp.Dispose()
    
    # Generate legacy ic_launcher_round.png
    $roundPath = Join-Path $folder "ic_launcher_round.png"
    Copy-Item $legacyPath $roundPath -Force
    
    # Generate adaptive foreground ic_launcher_foreground.png
    $fgBmp = New-Object System.Drawing.Bitmap $fgSize, $fgSize
    $g2 = [System.Drawing.Graphics]::FromImage($fgBmp)
    $g2.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g2.DrawImage($bmp, 0, 0, $fgSize, $fgSize)
    $g2.Dispose()
    $fgPath = Join-Path $folder "ic_launcher_foreground.png"
    $fgBmp.Save($fgPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $fgBmp.Dispose()
}

$bmp.Dispose()
Write-Host "Done!"
