@echo off
if exist %cd%\logs\pid.cat (
	echo 正在停止...
	del %cd%\logs\pid.cat
) else (
	echo 程序没有启动
	@pause
)
