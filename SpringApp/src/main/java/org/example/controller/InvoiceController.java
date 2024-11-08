package org.example.controller;

import org.example.dto.invoice.InvoiceCreateDTO;
import org.example.exception.InvoiceNotFoundException;
import org.example.model.Invoice;
import org.example.service.IInvoiceService;
import org.example.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private IInvoiceService service;

    @Autowired
    @Qualifier("storageServiceImpl") // Specify which bean to inject
    private StorageService storageService;

    @GetMapping("/register")
    public String showRegistration() {
        return "registerInvoicePage";
    }

    @PostMapping("/save")
    public String saveInvoice(
            @ModelAttribute InvoiceCreateDTO dto,
            RedirectAttributes attributes // Add RedirectAttributes here
    ) {
        try {
            // Save the invoice and get the saved entity
            Invoice savedInvoice = service.saveInvoice(dto);
            Long id = savedInvoice.getId();
            attributes.addAttribute("message", "Record with id: '" + id + "' is saved successfully!");
            return "redirect:getAllInvoices";
        } catch (Exception e) {
            attributes.addAttribute("message", "Error saving invoice: " + e.getMessage());
            return "redirect:/invoice/register";
        }
    }

    @GetMapping("/getAllInvoices")
    public String getAllInvoices(
            @RequestParam(value = "message", required = false) String message,
            Model model
    ) {
        List<Invoice> invoices = service.getAllInvoices();
        model.addAttribute("list", invoices);
        model.addAttribute("message", message);
        return "allInvoicesPage";
    }

    @GetMapping("/edit")
    public String getEditPage(
            Model model,
            RedirectAttributes attributes,
            @RequestParam Long id
    ) {
        String page = null;
        try {
            Invoice invoice = service.getInvoiceById(id);
            model.addAttribute("invoice", invoice);
            page = "editInvoicePage";
        } catch (InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", e.getMessage());
            page = "redirect:getAllInvoices";
        }
        return page;
    }

    @PostMapping("/update")
    public String updateInvoice(
            @ModelAttribute Invoice invoice,
            RedirectAttributes attributes
    ) {
        try {
            service.updateInvoice(invoice);
            Long id = invoice.getId();
            attributes.addAttribute("message", "Invoice with id: '" + id + "' is updated successfully!");
        } catch (InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", "Error updating invoice: " + e.getMessage());
        }
        return "redirect:getAllInvoices";
    }

    @GetMapping("/delete")
    public String deleteInvoice(
            @RequestParam Long id,
            RedirectAttributes attributes
    ) {
        try {
            service.deleteInvoiceById(id);
            attributes.addAttribute("message", "Invoice with Id: '" + id + "' is removed successfully!");
        } catch (InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", "Error deleting invoice: " + e.getMessage());
        }
        return "redirect:getAllInvoices";
    }

    @PostMapping("/upload/{invoiceId}")
    public String uploadInvoiceFile(
            @PathVariable Long invoiceId,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes attributes
    ) {
        try {
            // Save the uploaded file
            String fileName = storageService.save(file);

            // Retrieve the invoice and set the file name
            Invoice invoice = service.getInvoiceById(invoiceId);
            invoice.setFileName(fileName); // Update the invoice with the saved file name
            service.updateInvoice(invoice); // Save the updated invoice

            attributes.addAttribute("message", "File uploaded successfully and associated with invoice ID: " + invoiceId);
        } catch (IOException | InvoiceNotFoundException e) {
            e.printStackTrace();
            attributes.addAttribute("message", "Error uploading file: " + e.getMessage());
        }
        return "redirect:getAllInvoices";
    }
}
