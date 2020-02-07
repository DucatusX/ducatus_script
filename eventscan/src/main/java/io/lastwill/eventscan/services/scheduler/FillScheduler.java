package io.lastwill.eventscan.services.scheduler;

import io.lastwill.eventscan.repositories.DucatusTransitionEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//@Component
public class FillScheduler {
    @Autowired
    DucatusTransitionEntryRepository repository;

//    @PostConstruct
    public List<String> readLiners() {
        List<String> result = new ArrayList<>();
        try {
            File file = new File("/home/mikhail/Android/DucatusAddressess.txt");
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            result.add(line);
            while (line != null) {
//                DucatusTransitionEntry entry = new DucatusTransitionEntry(BigInteger.valueOf(55L));
//                entry.setAddress(line);
//                repository.save(entry);
//                System.out.println(line);
//                line = reader.readLine();
//                result.add(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println(i + " = " + result.get(i));
        }
        return result;
    }
}
