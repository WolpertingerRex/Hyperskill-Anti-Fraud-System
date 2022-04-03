package antifraud.presentation.controllers;

import antifraud.business.ip.IP;
import antifraud.business.ip.IPConstraint;
import antifraud.service.IPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/antifraud/suspicious-ip")
@Validated
public class IPController {
    @Autowired
    private IPService ipService;

    @GetMapping
    public List<IP> getAll(){
        return ipService.getAllIP();
    }

    @PostMapping
    public IP saveIP(@RequestBody @Valid IP ip){
        return ipService.saveIP(ip);
    }


    @DeleteMapping("/{ip}")
    public Map<String, String> deleteIP(@PathVariable @IPConstraint String ip){
        System.out.println("IP " + ip);
        ipService.deleteIP(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }
}

