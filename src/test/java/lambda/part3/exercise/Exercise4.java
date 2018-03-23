package lambda.part3.exercise;

import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.part3.example.Example1;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Exercise4 {

    private static class LazyCollectionHelper<T, R> {
        List<T> source;
        Function<T,List<R>> classMapper;

        private LazyCollectionHelper(List<T> source, Function<T, List<R>> classMapper) {
            this.source = source;
            this.classMapper = classMapper;
        }

        public static <T> LazyCollectionHelper<T, T> from(List<T> list) {
            return new LazyCollectionHelper<>(list, Collections::singletonList);
        }

        public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
            return new LazyCollectionHelper<>(source, v -> {
                List<U> list = new ArrayList<>();
                for (R r : classMapper.apply(v)) {
                    list.addAll(flatMapping.apply(r));
                }
                return list;
            });
        }

        public <U> LazyCollectionHelper<T, U> map(Function<R, U> mapping) {
            return new LazyCollectionHelper<>(source, v -> {
                List<U> list = new ArrayList<>();
                for (R r : classMapper.apply(v)) {
                    list.add(mapping.apply(r));
                }
                return list;
            });
        }

        public List<R> force() {
            List<R> list = new ArrayList<>();
            for (T l : source) {
                list.addAll(classMapper.apply(l));
            }
            return list;
        }
    }

    @Test
    public void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
        List<Employee> employees = Example1.getEmployees();
        List<Integer> codes = LazyCollectionHelper.from(employees)
                                                  .flatMap(Employee::getJobHistory)
                                                  .map(JobHistoryEntry::getPosition)
                                                  .flatMap(s -> {
                                                      List<Character> chs = new ArrayList<>();
                                                      for (int i = 0; i < s.length(); i++)
                                                          chs.add(s.charAt(i));
                                                      return chs;
                                                  })
                                                  .map(Integer::new)
                                                  .force();
        assertEquals(calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester", "QA", "QA", "QA", "dev"), codes);
    }

    private static List<Integer> calcCodes(String...strings) {
        List<Integer> codes = new ArrayList<>();
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                codes.add((int) letter);
            }
        }
        return codes;
    }
}