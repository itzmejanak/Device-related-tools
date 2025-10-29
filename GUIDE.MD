    一、 Bind/Unbind Station SN and IMEI.
Implement the corresponding logic requirements for the binding and unbinding interfaces on BindingController.
Package and update the binding tool to the static resource nginx root directory and provide the access path.

Note:
Please modify according to the actual interface.
    二、 Station Test tool
Implement/deploy the interface on YbtController and modify the web interface request path according to actual changes.
Package and update the test tool project to the static resource nginx root directory and provide the access path.

Note:
Try not to change the response content.
Modifying the unified response structure requires simultaneous modification of the web interface status check.



三、Compressed file contents
device-util-demo: This compressed package contains the interfaces for the device SN and IMEI binding tool and the device test tool.

web-cabinet-bind: This is the web page for the device SN and IMEI binding tool, using native HTML.

web-test-tool-demo: This is the web page for the device test tool, using VUE.