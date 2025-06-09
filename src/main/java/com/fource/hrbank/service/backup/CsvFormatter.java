package com.fource.hrbank.service.backup;

import com.fource.hrbank.dto.employee.EmployeeDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CsvFormatter {

    /**
     * EmployeeDto 리스트를 CSV 문자열로 변환합니다.
     *
     * @param employees 직원 정보 리스트
     * @return CSV 문자열
     */
    public static String toCsv(List<EmployeeDto> employees) {
        StringBuilder builder = new StringBuilder();

        builder.append("ID, 직원번호, 이름, 이메일, 부서, 직급, 입사일, 상태");
        builder.append(System.lineSeparator());

        for (EmployeeDto e : employees) {
            builder.append(String.join(",",
                    e.id().toString(),
                    e.employeeNumber(),
                    e.name(),
                    e.email(),
                    e.departmentName(),
                    e.position(),
                    e.hireDate().toString(),
                    e.status().name()
            ));
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
