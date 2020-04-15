if not "%minimized%"=="" goto :minimized
set minimized=true
start /min cmd /C "%~dpnx0"
goto :EOF
:minimized
color 0e
jupyter notebook --notebook-dir="C:\Users\clink\OneDrive\Archive\CS Classes\CS331"

