package hr.mmaracic.graphpr.api;

import hr.mmaracic.graphpr.service.age.AgeGraphDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("age")
@RequiredArgsConstructor
public class AgeController {

    private final AgeGraphDataService ageGraphDataService;

    @PostMapping(value = "embed")
    public String enrichQuery(@RequestParam String graphName, @RequestParam Integer paramCount, @RequestBody String cypherQuery) {
        return ageGraphDataService.embedCypherQuery(graphName, cypherQuery, paramCount);
    }

}
