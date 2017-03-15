package com.tgrigorov.postbox.services.data;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.data.entities.Person;
import com.tgrigorov.postbox.utils.IPredicate;
import com.tgrigorov.postbox.utils.ListUtils;

import java.util.List;

public class PersonService implements IPersonService {
    public Person loadByAddress(final String address) {
        Person person = null;

        List<Person> people = PostBox.getDbContext().getContext(Person.class).list();
        if (people != null) {
            List<Person> filtered = ListUtils.filter(people, new IPredicate<Person>() {
                public boolean filter(Person item) {
                    return item.getAddress().equals(address);
                }
            });
            person = ListUtils.firstOrDefault(filtered);
        }

        return person;
    }

    public Person create(final MailDetailModel.PersonInfo data) {
        Person person = loadByAddress(data.address);

        if (person == null) {
            person = new Person();
            person.setName(data.name);
            person.setAddress(data.address);
            person = PostBox.getDbContext().getContext(Person.class).create(person);
        }

        return person;
    }
}
