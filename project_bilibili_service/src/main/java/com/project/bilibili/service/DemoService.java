package project.bilibili.service;

import com.project.bilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DemoService {
    @Autowired
    private DemoDao demoDao;

    public String queryName(int id){
        return demoDao.queryName(id);
    }
}
