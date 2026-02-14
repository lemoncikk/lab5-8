package org.example.model;

import jakarta.xml.bind.annotation.*;
import org.example.command.CommandRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.IntStream;

@XmlRootElement(name="Context")
@XmlAccessorType(XmlAccessType.FIELD)
public class Context {
    @XmlElement(name="MusicBand")
    private Stack<MusicBand> store = new Stack<>();
    @XmlTransient
    public final CommandRegistry registry = new CommandRegistry();
    @XmlTransient
    private String[] args = null;

    public Context() {}

    public Stack<MusicBand> getStore() {
        return store;
    }

    public void setStore(Stack<MusicBand> store) {
        this.store = store;
    }

    public Optional<MusicBand> getById(int id) {
        return store.stream().filter(mb -> mb.getId() == id).findFirst();
    }

    public ArrayList<MusicBand> getAll() {
        return new ArrayList<>(store);
    }

    public void add(MusicBand mb) {
        store.push(mb);
    }

    public void removeById(int id) {
        store.removeIf(mb -> mb.getId() == id);
    }

    public void insertAt(int id, MusicBand mb) {

    }

    public void sort(boolean reverse_order) {

    }

    public void update(int id, MusicBand mb) {
        int stackIndex = IntStream.range(0, store.size())
                .filter(i -> store.get(i).getId() == id)
                .findFirst()
                .orElse(-1);
        mb.setId(id);
        if(stackIndex != -1) {
            store.remove(stackIndex);
            store.add(mb);
        } else add(mb);
    }

    public void saveToFile(String path) throws Exception {
        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(Context.class);
        jakarta.xml.bind.Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter s = new StringWriter();
        marshaller.marshal(this, s);
        var fw = new FileWriter(path);
        fw.write(s.toString());
        fw.flush();
        fw.close();
    }

    public static Context loadFromFile(String path) throws Exception {
        var sc = new Scanner(new File(path));
        sc.useDelimiter("\\A");
        String xmlContent = sc.hasNext() ? sc.next() : "";
        sc.close();

        jakarta.xml.bind.JAXBContext context = jakarta.xml.bind.JAXBContext.newInstance(Context.class);
        jakarta.xml.bind.Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Context) unmarshaller.unmarshal(new StringReader(xmlContent));
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
